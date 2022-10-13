package ru.gorshenev.themesstyles.presentation.ui.chat

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.presenter.RxPresenter
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import java.util.concurrent.TimeUnit

class ChatPresenter(private val repository: ChatRepository) :
    RxPresenter<ChatView>(ChatView::class.java) {

    private var displayedItems: List<ViewTyped> = listOf()

    private var reactionDisposable: Disposable? = null
    private var msgDisposable: Disposable? = null

    private lateinit var streamName: String
    private lateinit var topicName: String

    private var isLoading = false


    fun loadMessages(stream: String, topic: String) {
        streamName = stream
        topicName = topic

//        Single.concatArrayEager(
//            repository.getMessagesLocal(topicName)
//                .map { messageModels -> messageModels.toUi() },
//
//            repository.getMessagesFromApi(streamName, topicName)
//                .flatMap { messageResponse ->
//                    Observable.fromIterable(messageResponse.messages)
//                        .concatMapCompletable {
//                            repository.addToDatabase(it, topicName)
//                        }
//                        .toSingle { messageResponse.messages.toDomain().toUi() }
//                }
//        )
//            .debounce(400, TimeUnit.MILLISECONDS)
//            .materialize()
//            .filter { !it.isOnError }
//            .dematerialize { it }
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
                    view.stopLoading()
                    isLoading = false
                    view.scrollMsgsToTheEnd()
                    registerMessageQueue()
                    registerReactionQueue()
                },
                { err ->
                    view.stopLoading()
                    view.showError(err)
                },
            )
        //dispose on finish ошибка: 1 раз работает, второй нет  HTTP FAILED: java.io.InterruptedIOException
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
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(displayedItems)
                    isLoading = false
                },
                { err -> view.showError(err) },
            ).disposeOnFinish()
//        repository.uploadMoreMessages(firstMessageId, streamName, topicName)
//            .doOnSubscribe { isLoading = true }
//            .map { messageModels -> messageModels.toUi() }
//            .map { messages -> (displayedItems + messages).distinctBy { it.id }.sortedBy { it.id } }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { messages ->
//                    displayedItems = messages
//                    view.showItems(displayedItems)
//                    isLoading = false
//                },
//                { err -> view.showError(err) },
//            )
    }


    private lateinit var currentMessageQueueId: String
    private var lastMessageQueueId = -1

    private fun registerMessageQueue() {
        repository.registerMessageQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessageFromQueue(it.queueId, lastMessageQueueId) },
                { err -> view.showError(err) }
            ).disposeOnFinish()
    }

    private fun getMessageFromQueue(queueId: String, lastId: Int) {
        currentMessageQueueId = queueId
        /*
        turik
        иногда падает
        IllegalStateException: Fragment's view can't be accessed. Fragment isn't added
         дублируеи первое сообщение потому что lastMessageQueueId в onSuccess, если заменить
         на doOnEvent то все ок
         */
        msgDisposable = repository.getQueueMessages(currentMessageQueueId, lastId)
            .retry()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { response ->
                lastMessageQueueId = response.events.first().id
                val items = response.events.map { it.message }.toDomain().toUi()
                displayedItems = displayedItems + items
                view.showItems(displayedItems)
                view.scrollMsgsToTheEnd()
            }
            .flatMapCompletable {
                repository.saveMessage(it.events.first().message, topicName)
            }
            .subscribe(
                {
                    msgDisposable?.dispose()
                    getMessageFromQueue(currentMessageQueueId, lastMessageQueueId)
                },
                { err -> view.showError(err) })

//        my old
//        msgDisposable = repository.getQueueMessages(currentMessageQueueId, lastId)
//            .retry()
//            .flatMap { eventsResponse ->
//                lastMessageQueueId = eventsResponse.events.first().id
//                Single.just(eventsResponse.events.first().message)
//                    .flatMapCompletable { repository.saveMessage(it, topicName) }
//                    .toSingle { eventsResponse.events.map { it.message }.toDomain().toUi() }
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { items ->
//                    displayedItems = displayedItems + items
//                    view.showItems(displayedItems)
//                    view.scrollMsgsToTheEnd()
//                    msgDisposable?.dispose()
//                    getMessageFromQueue(currentMessageQueueId, lastMessageQueueId)
//                },
//                { err -> view.showError(err) })
    }


    private lateinit var currentReactionQueueId: String
    private var lastReactionQueueId = -1

    private fun registerReactionQueue() {
        repository.registerReactionQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getReactionEventsFromQueue(it.queueId, lastReactionQueueId) },
                { err -> view.showError(err) }
            ).disposeOnFinish()
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
                repository.saveMessage(it.message, topicName)
            }
            .subscribe(
                {
                    reactionDisposable?.dispose()
                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
                },
                { err -> view.showError(err) }
            )
//        reactionDisposable = repository.getQueueReactions(currentReactionQueueId, lastId)
//            .retry()
//            .flatMap { response ->
//                lastReactionQueueId = response.events.first().id
//                messageId = response.events.first().messageId
//                repository.getMessage(messageId)
//            }
//            .flatMap { response ->
//                Single.just(response.message)
//                    .flatMapCompletable { repository.addToDatabase(it, topicName) }
//                    .toSingle { response.message }
//            }
//            .map { message ->
//                updateMessage(displayedItems, messageId, listOf(message).toDomain().toUi().first())
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { items ->
//                    displayedItems = items
//                    view.showItems(displayedItems)
//                    reactionDisposable?.dispose()
//                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
//                },
//                { err -> view.showError(err) }
//            )
    }

    private fun updateMessage(
        newMessage: ViewTyped,
        currentItems: List<ViewTyped>
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
            ).disposeOnFinish()
    }

    fun sendMessage(messageText: String) {
        repository.sendMessage(messageText, streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ },
                { err -> view.showError(err) }
            ).disposeOnFinish()
    }

    fun onClear() {
        reactionDisposable?.dispose()
        msgDisposable?.dispose()
    }

}
