package ru.gorshenev.themesstyles.presentation.ui.chat

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.ChatApp
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import java.util.concurrent.TimeUnit

class ChatPresenter(private val view: ChatView) {

    private val db: AppDataBase by lazy { AppDataBase.getDataBase(ChatApp.appContext) }

    private val api: ZulipApi = Network.api

    private val repository: ChatRepository by lazy { ChatRepository(db.messageDao(), api) }

    private val compositeDisposable = CompositeDisposable()

    private var displayedItems: List<ViewTyped> = listOf()

    private var reactionDisposable: Disposable? = null
    private var msgDisposable: Disposable? = null

    private lateinit var streamName: String
    private lateinit var topicName: String

    private var isLoading = false


    fun loadMessages(stream: String, topic: String) {
        streamName = stream
        topicName = topic
        repository.getMessages(
            streamName = streamName,
            topicName = topicName,
            anchorMessageId = ChatRepository.DEFAULT_MESSAGE_ANCHOR,
            numBefore = ChatRepository.DEFAULT_NUM_BEFORE,
            onlyRemote = false
        )
            .debounce(400, TimeUnit.MILLISECONDS)
            .doOnSubscribe { isLoading = true }
            .map { it.toUi() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(displayedItems)
                    if (displayedItems.isNotEmpty()) view.stopLoading()
                    isLoading = false
                    registerMessageQueue()
                    registerReactionQueue()
                },
                { err ->
                    view.stopLoading()
                    view.showError(err)
                },
            ).apply { compositeDisposable.add(this) }
    }

    fun uploadMoreMessages() {
        if (isLoading) return
        repository.getMessages(
            streamName = streamName,
            topicName = topicName,
            anchorMessageId = displayedItems.first().id.toLong(),
            numBefore = ChatRepository.MORE_NUM_BEFORE,
            onlyRemote = true

        )
            .doOnSubscribe { isLoading = true }
            .map { messageModels -> messageModels.toUi() }
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
        repository.registerMessageQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessageFromQueue(it.queueId, lastMessageQueueId) },
                { err -> view.showError(err) }
            ).apply { compositeDisposable.add(this) }
    }

    private fun getMessageFromQueue(queueId: String, lastId: Int) {
        currentMessageQueueId = queueId
        msgDisposable = repository.getQueueMessages(currentMessageQueueId, lastId)
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { response ->
                lastMessageQueueId = response.events.last().id
                val items = response.events.map { it.message }.toDomain().toUi()
                displayedItems = displayedItems + items
                view.showItems(displayedItems)
                view.scrollMsgsToTheEnd()
            }
            .flatMapCompletable { repository.saveMessage(topicName, it.events.first().message) }
            .subscribe(
                {
                    msgDisposable?.dispose()
                    getMessageFromQueue(currentMessageQueueId, lastMessageQueueId)
                },
                { view.showError(it) }
            )
    }

    private lateinit var currentReactionQueueId: String
    private var lastReactionQueueId = -1

    private fun registerReactionQueue() {
        repository.registerReactionQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getReactionEventsFromQueue(it.queueId, lastReactionQueueId) },
                { err -> view.showError(err) }
            ).apply { compositeDisposable.add(this) }
    }

    private fun getReactionEventsFromQueue(queueId: String, lastId: Int) {
        currentReactionQueueId = queueId
        reactionDisposable = repository.getQueueReactions(currentReactionQueueId, lastId)
            .retry()
            .flatMap {
                val event = it.events.first()
                lastReactionQueueId = event.id
                repository.getMessage(event.messageId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable {
                updateMessage(listOf(it.message).toDomain().toUi().single(), displayedItems)
                repository.saveMessage(topicName, it.message)
            }.subscribe(
                {
                    reactionDisposable?.dispose()
                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
                },
                { view.showError(it) }
            )
    }

    private fun updateMessage(
        newMessage: ViewTyped,
        currentItems: List<ViewTyped>,
    ) {
        val updatedItems = currentItems.map { item ->
            when (item) {
                is MessageRightUi -> {
                    if (item.id == newMessage.id) {
                        item.copy(emojis = (newMessage as MessageRightUi).emojis)
                    } else
                        item
                }
                is MessageLeftUi -> {
                    if (item.id == newMessage.id) {
                        item.copy(emojis = (newMessage as MessageLeftUi).emojis)
                    } else
                        item
                }
                else -> item
            }
        }
        displayedItems = updatedItems
        view.showItems(displayedItems)
    }

    fun onEmojiClick(emojiName: String, messageId: Int) {
        repository.updateEmoji(emojiName, messageId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    fun sendMessage(message: String) {
        repository.sendMessage(message, streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ },
                { err -> view.showError(err) }
            ).apply { compositeDisposable.add(this) }
    }

    fun onClear() {
        reactionDisposable?.dispose()
        msgDisposable?.dispose()
        compositeDisposable.clear()
    }

//    private fun createMessageUi(
//        messages: List<MessageResponse>,
//        addToDatabase: Boolean
//    ): List<ViewTyped> {
//        return messages.map { message ->
////            val currentDate = Utils.getDateFromUnix(message.time)
////            var lastDate = ""
////
////            if (displayedItems.isNotEmpty()) {
////                lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
////            } else {
////                if (list.isEmpty()) list += DateUi(message.msgId, currentDate)
////                lastDate = (list.findLast { it is DateUi } as DateUi).text
////            }
////
////            if (lastDate != currentDate) list += DateUi(message.msgId, currentDate)
//            if (addToDatabase)
//                repository.addToDatabase(message, topicName)
//                    .subscribe({}, { e -> view.showError(e) })
//                    .apply { compositeDisposable.add(this) }
//
//            when (message.senderId) {
//                MY_USER_ID -> {
//                    MessageRightUi(
//                        id = message.msgId,
//                        text = message.content,
//                        time = Utils.getTimeFromUnix(message.time),
//                        emojis = createEmojiUi(message.reactions, message.msgId)
//                    )
//                }
//                else -> {
//                    MessageLeftUi(
//                        id = message.msgId,
//                        name = message.senderName,
//                        text = message.content,
//                        emojis = createEmojiUi(message.reactions, message.msgId),
//                        time = Utils.getTimeFromUnix(message.time),
//                        avatar = message.avatarUrl
//                    )
//                }
//            }
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
