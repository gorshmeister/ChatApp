package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState
import javax.inject.Inject

class GetQueueMessageMiddleware @Inject constructor(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.GetQueueMessage::class.java)
            .switchMap { action ->
                repository.getQueueMessages(action.queueId, action.lastId)
                    .retry()
                    .flatMapObservable { response ->
                        val message = response.events.first().message
                        val getMessageAction = repository.saveMessage(message, action.topicName)
                            .toSingleDefault(listOf(message))
                            .map<ChatAction> {
                                ChatAction.GetQueueMessage(
                                    queueId = action.queueId,
                                    lastId = response.events.first().id,
                                    topicName = action.topicName,
                                    items = it.toDomain().toUi()
                                )
                            }.toObservable()

                        val scrollAction = Observable.just(ChatInternalAction.ScrollToTheEnd)

                        Observable.concatArrayDelayError(getMessageAction, scrollAction)
                    }.onErrorReturn { ChatInternalAction.LoadError(it) }
            }
    }
}