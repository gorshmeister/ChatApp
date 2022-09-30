package ru.gorshenev.themesstyles.presentation.ui.chat

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.Message
import ru.gorshenev.themesstyles.data.network.model.Narrow
import ru.gorshenev.themesstyles.data.network.model.Reaction
import ru.gorshenev.themesstyles.data.network.model.ReactionAddOrRemove
import ru.gorshenev.themesstyles.data.repositories.Reactions.MY_USER_ID
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.utils.Utils.toEmojiCode
import java.util.concurrent.TimeUnit

class ChatPresenter(private val view: ChatView) {

    private val compositeDisposable = CompositeDisposable()

    private var displayedItems: List<ViewTyped> = listOf()

    private var streamName = ""

    private var topicName = ""

    private lateinit var reactionDisposable: Disposable

    private lateinit var msgDisposable: Disposable

    private val api: ZulipApi = Network.api


    fun setStreamAndTopicNames(stream: String, topic: String) {
        streamName = stream
        topicName = topic
    }

    fun loadMessages() {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )

        api.getMessages(
            anchor = 10000000000000000,
            numBefore = 10,
            numAfter = 0,
            narrow = narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .map { response -> createMessageUi(response.messages) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = displayedItems + messages
                    view.showItems(displayedItems)
                },
                { err -> view.showError(err) },
                { view.stopLoading() }
            ).apply { compositeDisposable.add(this) }
    }

    fun uploadMoreMessages() {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )

        api.getMessages(
            displayedItems.first().id.toLong(),
            10,
            0,
            narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .map { response -> createMessageUi(response.messages) }
            .map { messages -> (displayedItems + messages).toSet() }
            .map { set -> set.sortedBy { it.id } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(displayedItems)
                },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    private var currentQueueId = ""
    private var lastQueueId = -1

    fun registerMessageQueue() {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("message"))

        api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessageFromQueue(it.queueId, lastQueueId) },
                { err -> view.showError(err) }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun getMessageFromQueue(queueId: String, lastId: Int) {
        currentQueueId = queueId
        msgDisposable = api.getEventsFromQueue(currentQueueId, lastId)
            .retry()
            .doOnNext { response ->
                lastQueueId = response.events.last().id
                Log.d("event", "lastqueueId $lastQueueId")
            }
            .map { response -> createMessageUi(response.events.map { it.message }) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = displayedItems + items
                    view.showItems(displayedItems)
                    view.scrollMsgsToTheEnd()
                    msgDisposable.dispose()
                    getMessageFromQueue(currentQueueId, lastQueueId)
                    Log.d("qweqwe", items.toString())
                },
                { err -> view.showError(err) }
            )
    }


    private var currentReactionQueueId = ""
    private var lastReactionQueueId = -1

    fun registerReactionQueue() {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("reaction"))

        api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getReactionEventsFromQueue(it.queueId, lastReactionQueueId) },
                { err -> view.showError(err) }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun getReactionEventsFromQueue(queueId: String, lastId: Int) {
        currentReactionQueueId = queueId
        reactionDisposable = api.getEmojiEventsFromQueue(currentReactionQueueId, lastId)
            .retry()
            .doOnNext { response -> lastReactionQueueId = response.events.last().id }
            .map { response ->
                val event = response.events.first()
                actionWithReaction(
                    messages = displayedItems,
                    emojiName = event.emojiName,
                    emojiCode = event.emojiCode,
                    messageId = event.messageId,
                    userId = event.userId,
                    addOrRemove = event.addOrRemove
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = items
                    view.showItems(displayedItems)
                    reactionDisposable.dispose()
                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
                },
                { err -> view.showError(err) }
            )
    }

    private fun actionWithReaction(
        messages: List<ViewTyped>,
        emojiName: String,
        emojiCode: String,
        messageId: Int,
        userId: Int,
        addOrRemove: ReactionAddOrRemove
    ): List<ViewTyped> {
        return messages.map { item ->
            val add = addOrRemove == ReactionAddOrRemove.ADD
            val remove = addOrRemove == ReactionAddOrRemove.REMOVE

            when (item) {
                is MessageRightUi -> {
                    var updatedEmojis = item.emojis
                    val isMyReaction = userId == MY_USER_ID


                    val isEmojiExists = item.emojis.map { it.name }.contains(emojiName)

                    if (isEmojiExists) {
                        updatedEmojis = item.emojis.map {
                            val isTargetEmoji = it.name == emojiName && item.id == messageId
                            val didIClick = it.listUsersId.contains(MY_USER_ID)

                            when {
                                isMyReaction -> when {
                                    isTargetEmoji && !didIClick && add  -> it.copy(
                                        isSelected = true,
                                        listUsersId = it.listUsersId + listOf(MY_USER_ID),
                                        counter = it.counter + 1
                                    )
                                    isTargetEmoji && didIClick && remove -> it.copy(
                                        isSelected = false,
                                        listUsersId = it.listUsersId - listOf(MY_USER_ID),
                                        counter = it.counter - 1
                                    )
                                    else -> it
                                }
                                !isMyReaction -> when {
                                    isTargetEmoji && add -> it.copy(
                                        isSelected = didIClick,
                                        listUsersId = it.listUsersId + listOf(userId),
                                        counter = it.counter + 1
                                    )
                                    isTargetEmoji && remove -> it.copy(
                                        isSelected = didIClick,
                                        listUsersId = it.listUsersId - listOf(userId),
                                        counter = it.counter - 1
                                    )
                                    else -> it
                                }
                                else -> it
                            }
                        }
                    } else if (!isEmojiExists && item.id == messageId && add) {
                        updatedEmojis = item.emojis + EmojiUi(
                            msgId = messageId,
                            name = emojiName,
                            code = emojiCode.toEmojiCode(),
                            listUsersId = listOf(userId),
                            counter = 1,
                            isSelected = isMyReaction
                        )

                    }

                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                is MessageLeftUi -> {
                    var updatedEmojis = item.emojis
                    val isMyReaction = userId == MY_USER_ID

                    val isEmojiExists = item.emojis.map { it.name }.contains(emojiName)

                    if (isEmojiExists) {
                        updatedEmojis = item.emojis.map {
                            val isTargetEmoji = it.name == emojiName && item.id == messageId
                            val didIClick = it.listUsersId.contains(MY_USER_ID)

                            when {
                                isMyReaction -> when {
                                    isTargetEmoji && !didIClick && add -> it.copy(
                                        isSelected = true,
                                        listUsersId = it.listUsersId + listOf(MY_USER_ID),
                                        counter = it.counter + 1
                                    )
                                    isTargetEmoji && didIClick && remove -> it.copy(
                                        isSelected = false,
                                        listUsersId = it.listUsersId - listOf(MY_USER_ID),
                                        counter = it.counter - 1
                                    )
                                    else -> it
                                }
                                !isMyReaction -> when {
                                    isTargetEmoji && add -> it.copy(
                                        isSelected = didIClick,
                                        listUsersId = it.listUsersId + listOf(userId),
                                        counter = it.counter + 1
                                    )
                                    isTargetEmoji && remove -> it.copy(
                                        isSelected = didIClick,
                                        listUsersId = it.listUsersId - listOf(userId),
                                        counter = it.counter - 1
                                    )
                                    else -> it
                                }
                                else -> it
                            }
                        }
                    } else if (!isEmojiExists && item.id == messageId && add) {
                        updatedEmojis = item.emojis + EmojiUi(
                            msgId = messageId,
                            name = emojiName,
                            code = emojiCode.toEmojiCode(),
                            listUsersId = listOf(userId),
                            counter = 1,
                            isSelected = isMyReaction
                        )
                    }

                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                else -> item
            }
        }
    }

    fun onEmojiClick(emojiName: String, messageId: Int) {
        Observable.just(updateEmojisCounter(displayedItems, emojiName, messageId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    private fun updateEmojisCounter(
        messages: List<ViewTyped>,
        emojiName: String,
        messageId: Int,
    ) {
        return messages.forEach { item ->
            when (item) {
                is MessageRightUi -> {
                    item.emojis.map {
                        val isTargetEmoji = it.name == emojiName && item.id == messageId
                        val isMeClicked = it.listUsersId.contains(MY_USER_ID)
                        when {
                            isTargetEmoji && !isMeClicked -> {
                                api.addEmoji(messageId, emojiName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({}, { err -> view.showError(err) })
                                    .apply { compositeDisposable.add(this) }
                                it
                            }
                            isTargetEmoji && isMeClicked -> {
                                api.deleteEmoji(messageId, emojiName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({}, { err -> view.showError(err) })
                                    .apply { compositeDisposable.add(this) }
                                it
                            }
                            else -> it
                        }
                    }
                }
                is MessageLeftUi -> {
                    item.emojis.map {
                        val isTargetEmoji = it.name == emojiName && item.id == messageId
                        val isMeClicked = it.listUsersId.contains(MY_USER_ID)
                        when {
                            isTargetEmoji && !isMeClicked -> {
                                api.addEmoji(messageId, emojiName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({}, { err -> view.showError(err) })
                                    .apply { compositeDisposable.add(this) }
                                it
                            }
                            isTargetEmoji && isMeClicked -> {
                                api.deleteEmoji(messageId, emojiName)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({}, { err -> view.showError(err) })
                                    .apply { compositeDisposable.add(this) }
                                it
                            }
                            else -> it
                        }
                    }
                }
            }
        }
    }

    fun addReactionFromBottomSheet(emojiName: String, messageId: Int) {
        Observable.just(addReactions(displayedItems, messageId, emojiName))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    private fun addReactions(
        items: List<ViewTyped>,
        messageId: Int,
        emojiName: String
    ) {
        return items.forEach { item ->
            when (item) {
                is MessageRightUi -> {
                    if (item.id == messageId) {
                        val isEmojiExists = item.emojis.map { it.name }.contains(emojiName)
                        if (isEmojiExists) {
                            view.showToast()
                        } else {
                            api.addEmoji(messageId, emojiName)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({}, { err -> view.showError(err) })
                                .apply { compositeDisposable.add(this) }
                        }
                    }
                }
                is MessageLeftUi -> {
                    if (item.id == messageId) {
                        val isEmojiExists = item.emojis.map { it.name }.contains(emojiName)
                        if (isEmojiExists) {
                            view.showToast()
                        } else {
                            api.addEmoji(messageId, emojiName)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({}, { err -> view.showError(err) })
                                .apply { compositeDisposable.add(this) }
                        }
                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        api.sendMessage(
            to = streamName,
            topic = topicName,
            content = message
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ },
                { err -> view.showError(err) }
            ).apply { compositeDisposable.add(this) }
    }

    fun onClear() {
        compositeDisposable.clear()
        reactionDisposable.dispose()
        msgDisposable.dispose()
    }

    private fun createMessageUi(
        messages: List<Message>,
    ): List<ViewTyped> {
        return messages.map { message ->
//            val currentDate = Utils.getDateFromUnix(message.time)
//            var lastDate = ""
//
//            if (displayedItems.isNotEmpty()) {
//                lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
//            } else {
//                if (list.isEmpty()) list += DateUi(message.msgId, currentDate)
//                lastDate = (list.findLast { it is DateUi } as DateUi).text
//            }
//
//            if (lastDate != currentDate) list += DateUi(message.msgId, currentDate)


            when (message.senderId) {
                MY_USER_ID -> {
                    MessageRightUi(
                        id = message.msgId,
                        text = message.content,
                        time = Utils.getTimeFromUnix(message.time),
                        emojis = createEmojiUi(message.reactions, message.msgId)
                    )
                }
                else -> {
                    MessageLeftUi(
                        id = message.msgId,
                        name = message.senderName,
                        text = message.content,
                        emojis = createEmojiUi(message.reactions, message.msgId),
                        time = Utils.getTimeFromUnix(message.time),
                        avatar = message.avatarUrl
                    )
                }
            }
        }
    }

    private fun createEmojiUi(reactions: List<Reaction>, messageId: Int): List<EmojiUi> {
        val list = mutableListOf<EmojiUi>()
        reactions.forEach { reaction ->
            val sameEmojiUi = list.find { it.name == reaction.emojiName }

            if (sameEmojiUi != null) {
                val index = list.indexOf(sameEmojiUi)
                list.remove(sameEmojiUi)

                val updItem = sameEmojiUi.copy(
                    listUsersId = sameEmojiUi.listUsersId + listOf(reaction.userId),
                    counter = sameEmojiUi.counter + 1,
                    isSelected = reactions.any { it.userId == MY_USER_ID }
                )
                list.add(index, updItem)
            } else {
                list += EmojiUi(
                    msgId = messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    counter = +1,
                    isSelected = reactions.any { it.userId == MY_USER_ID }
                )
            }
        }
        return list.toList()
    }


    //    private fun getReactionEventsFromQueue(queueId: String, lastId: Int) {
//        var messageId = 0
//        currentReactionQueueId = queueId
//        api.getEmojiEventsFromQueue(currentReactionQueueId, lastId)
//            .retry()
//            .doOnNext { response ->
//                lastReactionQueueId = response.events.last().id
//                Log.d("event", "lastReactionQueueId $lastReactionQueueId")
//            }
//            .concatMap { response ->
//                messageId = response.events.first().messageId
//                api.getMessage(response.events.first().messageId)
//            }
//            .map { response -> createMessageUi(listOf(response.message)) }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { items ->
//                    displayedItems = updateEmojis(displayedItems, messageId, items.first())
//                    view.showItems(displayedItems)
//                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
//                    Log.d("qweqwe", items.toString())
//                },
//                { err -> view.showError(err) }
//            ).apply { compositeDisposable.add(this) }
//
//    }

    //    private fun updateEmojis(
//        items: List<ViewTyped>,
//        messageId: Int,
//        updatedMessage: ViewTyped
//    ): List<ViewTyped> {
//        return items.map { item ->
//            when (item) {
//                is MessageRightUi -> {
//                    if (item.id == messageId) {
//                        item.copy(emojis = (updatedMessage as MessageRightUi).emojis)
//
//                    } else
//                        item
//                }
//                is MessageLeftUi -> {
//                    if (item.id == messageId) {
//                        item.copy(emojis = (updatedMessage as MessageLeftUi).emojis)
//
//                    } else
//                        item
//                }
//                else -> item
//            }
//        }
//    }

//    fun onEmojiClick(emojiName: String, messageId: Int) {
//        //todo удалить лишний запрос
//        api.getMessage(id = messageId, apply_markdown = true)
//            .map { response -> response.message.reactions.filter { reaction -> reaction.emojiName == emojiName } }
//            .map { reactions -> reactions.any { it.userId == MY_USER_ID } }
//            .concatMapSingle { isMyClick -> updateEmoji(messageId, emojiName, isMyClick) }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {
////                    view.updateMessages()
//                },
//                { err -> view.showError(err) },
//            ).apply { compositeDisposable.add(this) }
//    }

//    private fun updateEmoji(
//        msgId: Int,
//        emojiName: String,
//        isMyClick: Boolean = false
//    ): Single<CreateReactionResponse> {
//        return when {
//            isMyClick -> api.deleteEmoji(msgId = msgId, emojiName)
//            else -> api.addEmoji(msgId = msgId, emojiName)
//        }
//    }


//    private fun addReactions(
//        items: List<ViewTyped>,
//        messageId: Int,
//        emojiCode: Int,
//    ): Single<List<ViewTyped>> {
//        return Single.fromCallable {
//            items.map { item ->
//                when (item) {
//                    is MessageRightUi -> {
//                        if (item.id == messageId) {
//                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
//                            if (isEmojiExists) {
//                                throw Errors.ReactionAlreadyExist()
//                            } else {
//                                item.copy(
//                                    emojis = item.emojis + EmojiUi(
//                                        code = emojiCode,
//                                        counter = 1,
//                                        isSelected = true,
//                                        msgId = messageId,
//                                        listUsersId = listOf(MY_USER_ID),
//                                    )
//                                )
//                            }
//                        } else {
//                            item
//                        }
//                    }
//                    is MessageLeftUi -> {
//                        if (item.id == messageId) {
//                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
//                            if (isEmojiExists) {
//                                throw Errors.ReactionAlreadyExist()
//                            } else {
//                                item.copy(
//                                    emojis = item.emojis + EmojiUi(
//                                        code = emojiCode,
//                                        counter = 1,
//                                        isSelected = true,
//                                        msgId = messageId,
//                                        listUsersId = listOf(MY_USER_ID),
//                                    )
//                                )
//                            }
//                        } else {
//                            item
//                        }
//                    }
//                    else -> item
//                }
//            }
//        }
//
//    }

//    private fun updateEmojisCounter(
//        messages: List<ViewTyped>,
//        emojiCode: Int,
//        messageId: Int
//    ): Single<List<ViewTyped>> {
//        return Single.fromCallable {
//            messages.map { item ->
//                when (item) {
//                    is MessageRightUi -> {
//                        val updatedEmojis = item.emojis.map {
//                            val isTargetEmoji = it.code == emojiCode && item.id == messageId
//                            val isMeClicked = it.listUsersId.contains(MY_USER_ID)
//                            when {
//                                isTargetEmoji && !isMeClicked -> {
//                                    it.copy(
//                                        isSelected = true,
//                                        listUsersId = it.listUsersId + listOf(MY_USER_ID),
//                                        counter = it.counter + 1
//                                    )
//                                }
//                                isTargetEmoji && isMeClicked -> {
//                                    it.copy(
//                                        isSelected = false,
//                                        listUsersId = it.listUsersId - listOf(MY_USER_ID),
//                                        counter = it.counter - 1
//                                    )
//                                }
//                                else -> it
//                            }
//                        }
//                        item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
//                    }
//                    is MessageLeftUi -> {
//                        val updatedEmojis = item.emojis.map {
//                            if (it.code == emojiCode && item.id == messageId && !it.listUsersId.contains(
//                                    MY_USER_ID
//                                )
//                            ) {
//                                it.copy(
//                                    isSelected = true,
//                                    listUsersId = it.listUsersId + listOf(MY_USER_ID),
//                                    counter = it.counter + 1
//                                )
//                            } else if (it.code == emojiCode && item.id == messageId && it.listUsersId.contains(
//                                    MY_USER_ID
//                                )
//                            ) {
//                                it.copy(
//                                    isSelected = false,
//                                    listUsersId = it.listUsersId - listOf(MY_USER_ID),
//                                    counter = it.counter - 1
//                                )
//                            } else {
//                                it
//                            }
//                        }
//                        item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
//                    }
//                    else -> item
//                }
//            }
//        }
//    }

//    fun loadMessages(count: Int) {
//        ChatDataSource.getMessage(count)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { messages ->
//                    displayedItems = messages
//                    view.showItems(messages)
//                },
//                { error -> view.showError(error) }
//            )
//            .apply { compositeDisposable.add(this) }
//    }

//    fun sendMessage(message: String) {
//        val lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
//        if (lastDate != Utils.getCurrentDate()) {
//            displayedItems = displayedItems + DateUi(
//                id = displayedItems.size + 1,
//                text = Utils.getCurrentDate(),
//            )
//        }
//
//        try {
//            displayedItems = displayedItems + MessageRightUi(
//                id = displayedItems.size + 1,
//                text = message,
//                time = Utils.getCurrentTime(),
//                emojis = emptyList()
//            )
//            if (displayedItems.size % 5 == 0) {
//                throw Errors.MessageError("Owi6ka oTnpaBku coo6weHu9I")
//            }
//        } catch (e: Errors.MessageError) {
//            view.showError(e)
//        }
//
//        view.showItems(displayedItems)
//    }

//    fun addReaction(resultPick: BottomSheet.EmojiPickResult) {
//        Single.just(resultPick)
//            .flatMap { (id, code) ->
//                addReactions(displayedItems, id, code)
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { updList ->
//                    displayedItems = updList
//                    view.showItems(updList)
//                },
//                { error ->
//                    when (error) {
//                        is Errors.ReactionAlreadyExist -> view.showToast()
//                        else -> view.showError(error)
//                    }
//                })
//            .apply { compositeDisposable.add(this) }
//    }

//    fun onEmojiClick(emojiCode: Int, messageId: Int) {
//        Single.just(emojiCode to messageId)
//            .flatMap { (code, id) ->
//                updateEmojisCounter(displayedItems, code, id)
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { updList ->
//                    displayedItems = updList
//                    view.showItems(updList)
//                },
//                { error -> view.showError(error) })
//            .apply { compositeDisposable.add(this) }
//    }


}
