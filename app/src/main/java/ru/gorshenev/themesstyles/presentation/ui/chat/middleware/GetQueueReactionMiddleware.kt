package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi

class GetQueueReactionMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {

    private var lastId = -1

    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.GetQueueReaction::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .switchMap { (action, state) ->
                repository.getQueueReactions(action.queueId, action.lastId)
                    .toObservable()
                    .retry()
                    .flatMap { response ->
                        val event = response.events.single()
                        lastId = event.id
                        repository.getMessage(event.messageId).toObservable()
                    }
                    .flatMap { response ->
                        Observable.just(response.message)
                            .flatMapCompletable { repository.saveMessage(it, action.topicName) }
                            .toSingle {
                                updateMessage(
                                    newMessage = listOf(response.message).toDomain().toUi().first(),
                                    currentItems = if (state is ChatState.Result) state.items else emptyList()
                                )
                            }
                            .toObservable()
                    }.map<ChatAction> {
                        ChatAction.GetQueueReaction(action.queueId, lastId, action.topicName, it)
                    }
                    .onErrorReturn { ChatInternalAction.LoadError(it) }
            }
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

}