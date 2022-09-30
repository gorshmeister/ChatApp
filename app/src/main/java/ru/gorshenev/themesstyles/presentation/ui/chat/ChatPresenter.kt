package ru.gorshenev.themesstyles.presentation.ui.chat

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.CreateReactionResponse
import ru.gorshenev.themesstyles.data.network.model.Message
import ru.gorshenev.themesstyles.data.network.model.Narrow
import ru.gorshenev.themesstyles.data.network.model.Reaction
import ru.gorshenev.themesstyles.data.repositories.chat.Reactions.MY_USER_ID
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.utils.Utils.toEmojiCode

class ChatPresenter(private val view: ChatView) {

    private val compositeDisposable = CompositeDisposable()

    private var displayedItems: List<ViewTyped> = listOf()

    private var reactionDisposable: Disposable? = null
    private var msgDisposable: Disposable? = null

    private val api: ZulipApi = Network.api

    private lateinit var streamName: String
    private lateinit var topicName: String

    private var isLoading = false


    fun loadMessagesFromDatabase(stream: String, topic: String) {
        streamName = stream
        topicName = topic
        view.repository().getMessagesFromDb(topic)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = displayedItems + messages
                    view.showItems(displayedItems)
                    if (displayedItems.isNotEmpty()) view.stopLoading()
                },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    fun loadMessagesFromApi() {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )
//todo либо загружать 50 либо 20
        api.getMessages(
            anchor = 10000000000000000,
            numBefore = 50,
            numAfter = 0,
            narrow = narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .map { response -> createMessageUi(response.messages, true) }
            .doOnSubscribe { isLoading = true }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    Log.d("database", "==== Messages loaded from network ==== ")
                    displayedItems = messages
                    view.showItems(displayedItems)
                    isLoading = false
                    registerMessageQueue()
                    registerReactionQueue()
                },
                { err -> view.showError(err) },
                { view.stopLoading() }
            ).apply { compositeDisposable.add(this) }
    }

    fun uploadMoreMessages() {
        if (isLoading) return
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )

        api.getMessages(
            displayedItems.first().id.toLong(),
            20,
            0,
            narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .doOnSubscribe { isLoading = true }
            .map { response -> createMessageUi(response.messages, false) }
            .map { messages -> (displayedItems + messages).distinctBy { it.id }.sortedBy { it.id } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(displayedItems)
                    isLoading = false
                },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }


    private lateinit var currentMessageQueueId: String
    private var lastMessageQueueId = -1

    private fun registerMessageQueue() {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("message"))

        api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessageFromQueue(it.queueId, lastMessageQueueId) },
                { err -> view.showError(err) }
            )
            .apply { compositeDisposable.add(this) }
    }

    private fun getMessageFromQueue(queueId: String, lastId: Int) {
        currentMessageQueueId = queueId
        msgDisposable = api.getEventsFromQueue(currentMessageQueueId, lastId)
            .retry()
            .doOnNext { response -> lastMessageQueueId = response.events.last().id }
            .map { response -> createMessageUi(response.events.map { it.message }, true) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = displayedItems + items
                    view.showItems(displayedItems)
                    view.scrollMsgsToTheEnd()
                    msgDisposable?.dispose()
                    getMessageFromQueue(currentMessageQueueId, lastMessageQueueId)
                },
                { err -> view.showError(err) }
            )
    }


    private lateinit var currentReactionQueueId: String
    private var lastReactionQueueId = -1

    private fun registerReactionQueue() {
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
        var messageId = 0
        currentReactionQueueId = queueId
        reactionDisposable = api.getEmojiEventsFromQueue(currentReactionQueueId, lastId)
            .retry()
            .doOnNext { response -> lastReactionQueueId = response.events.first().id }
            .concatMap { response ->
                messageId = response.events.first().messageId
                api.getMessage(messageId)
            }
            .map { response ->
                updateMessage(
                    displayedItems,
                    messageId,
                    createMessageUi(listOf(response.message), true).first()
                )
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = items
                    view.showItems(displayedItems)
                    reactionDisposable?.dispose()
                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
                },
                { err -> view.showError(err) }
            )
    }

    private fun updateMessage(
        items: List<ViewTyped>,
        messageId: Int,
        updatedMessage: ViewTyped
    ): List<ViewTyped> {
        return items.map { item ->
            when (item) {
                is MessageRightUi -> {
                    if (item.id == messageId) {
                        item.copy(emojis = (updatedMessage as MessageRightUi).emojis)
                    } else
                        item
                }
                is MessageLeftUi -> {
                    if (item.id == messageId) {
                        item.copy(emojis = (updatedMessage as MessageLeftUi).emojis)
                    } else
                        item
                }
                else -> item
            }
        }
    }

    fun onEmojiClick(emojiName: String, messageId: Int) {
        api.getMessage(id = messageId, applyMarkdown = true)
            .map { response ->
                response.message.reactions.filter { reaction -> reaction.emojiName == emojiName }
                    .any { it.userId == MY_USER_ID }
            }
            .concatMapSingle { isMyClick -> updateEmoji(messageId, emojiName, isMyClick) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    private fun updateEmoji(
        msgId: Int,
        emojiName: String,
        isMyClick: Boolean = false
    ): Single<CreateReactionResponse> {
        return when {
            isMyClick -> api.deleteEmoji(msgId = msgId, emojiName)
            else -> api.addEmoji(msgId = msgId, emojiName)
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
        reactionDisposable?.dispose()
        msgDisposable?.dispose()
//        if (this::reactionDisposable::isInitialized.get()) reactionDisposable.dispose()
//        if (this::msgDisposable::isInitialized.get()) msgDisposable.dispose()
        compositeDisposable.clear()
    }

    private fun createMessageUi(
        messages: List<Message>,
        addToDatabase: Boolean
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
            if (addToDatabase)
                view.repository().addToDatabase(message, topicName).subscribe().apply {
                    compositeDisposable.add(this)
                }

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
                    isSelected = reaction.userId == MY_USER_ID
                )
                list.add(index, updItem)
            } else {
                list += EmojiUi(
                    msgId = messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    counter = +1,
                    isSelected = reaction.userId == MY_USER_ID
                )
            }
        }
        return list.toList()
    }

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
