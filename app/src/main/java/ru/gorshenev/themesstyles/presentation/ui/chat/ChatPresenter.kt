package ru.gorshenev.themesstyles.presentation.ui.chat

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.presenter.RxPresenter
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import java.util.concurrent.TimeUnit

class ChatPresenter(private val repository: ChatRepository) :
    RxPresenter<ChatView>() {

    private lateinit var streamName: String

    private lateinit var topicName: String

    private var displayedItems: List<ViewTyped> = listOf()

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
                    view?.showItems(displayedItems)
                    isLoading = false
                    view?.scrollToTheEnd()
                    view?.stopLoading()
                    registerMessageQueue()
                    registerReactionQueue()
                },
                { err ->
                    view?.stopLoading()
                    view?.showError(err)
                },
            ).disposeOnFinish()
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
                    view?.showItems(displayedItems)
                    isLoading = false
                },
                { err -> view?.showError(err) },
            ).disposeOnFinish()
    }


    private lateinit var currentMessageQueueId: String
    private var lastMessageQueueId = -1

    private fun registerMessageQueue() {
        repository.registerMessageQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessageFromQueue(it.queueId, lastMessageQueueId) },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }

    private fun getMessageFromQueue(queueId: String, lastId: Int) {
        currentMessageQueueId = queueId
        repository.getQueueMessages(currentMessageQueueId, lastId)
            .retry()
            .flatMap { eventsResponse ->
                lastMessageQueueId = eventsResponse.events.first().id
                Single.just(eventsResponse.events.first().message)
                    .flatMapCompletable { repository.saveMessage(it, topicName) }
                    .toSingle { eventsResponse.events.map { it.message }.toDomain().toUi() }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = displayedItems + items
                    view?.showItems(displayedItems)
                    view?.scrollToTheEnd()
                    getMessageFromQueue(currentMessageQueueId, lastMessageQueueId)
                },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }


    private lateinit var currentReactionQueueId: String
    private var lastReactionQueueId = -1

    private fun registerReactionQueue() {
        repository.registerReactionQueue(streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getReactionEventsFromQueue(it.queueId, lastReactionQueueId) },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }

    private lateinit var reactionDisposable: Disposable

    private fun getReactionEventsFromQueue(queueId: String, lastId: Int) {
        currentReactionQueueId = queueId
        reactionDisposable = repository.getQueueReactions(currentReactionQueueId, lastId)
            .retry()
            .flatMap { response ->
                val event = response.events.single()
                lastReactionQueueId = event.id
                repository.getMessage(event.messageId)
            }
            .flatMap { response ->
                Single.just(response.message)
                    .flatMapCompletable { repository.saveMessage(it, topicName) }
                    .toSingle {
                        updateMessage(
                            listOf(response.message).toDomain().toUi().first(),
                            displayedItems
                        )
                    }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    displayedItems = it
                    view?.showItems(displayedItems)
                    reactionDisposable.dispose()
                    getReactionEventsFromQueue(currentReactionQueueId, lastReactionQueueId)
                },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }

    private fun updateMessage(
        newMessage: ViewTyped,
        currentItems: List<ViewTyped>
    ): List<ViewTyped> {
        return currentItems.map { item ->
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
    }

    fun onEmojiClick(emojiName: String, messageId: Int, isBottomSheetClick: Boolean = false) {
        repository.updateEmoji(emojiName, messageId, isBottomSheetClick)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {},
                { err ->
                    when (err) {
                        is Errors.ReactionAlreadyExist -> view?.showReactionExistsToast()
                        else -> view?.showError(err)
                    }
                },
            ).disposeOnFinish()
    }

    fun sendMessage(messageText: String) {
        repository.sendMessage(messageText, streamName, topicName)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ },
                { err -> view?.showError(err) }
            ).disposeOnFinish()
    }

}
