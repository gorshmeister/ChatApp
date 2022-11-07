package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
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
            .switchMap { action ->
                repository.getQueueReactions(action.queueId, action.lastId)
                    .retry()
                    .flatMapObservable { response ->
                        val event = response.events.single()
                        lastId = event.id
                        repository.getMessage(event.messageId).toObservable()
                    }
                    .withLatestFrom(state) { response, currentState -> response to currentState }
                    .filter { (_, currentState) -> currentState is ChatState.Result }
                    .flatMap<ChatAction> { (response, currentState) ->
                        val updatedItems = updateMessage(
                            newMessage = listOf(response.message).toDomain().toUi().first(),
                            currentItems = (currentState as ChatState.Result).items
                        )
                        val resultAction = ChatAction.GetQueueReaction(
                            queueId = action.queueId,
                            lastId = lastId,
                            topicName = action.topicName,
                            items = updatedItems
                        )
                        repository.saveMessage(response.message, action.topicName)
                            .toSingleDefault(resultAction)
                            .toObservable()
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