package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState

class GetQueueMessageMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    private var lastId = -1

    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.GetQueueMessage::class.java)
            .switchMap { action->
                repository.getQueueMessages(action.queueId, action.lastId)
                    .toObservable()
                    .retry()
                    .flatMap { response ->
                        lastId = response.events.first().id
                        Single.just(response.events.first().message)
                            .flatMapCompletable { repository.saveMessage(it, action.topicName) }
                            .toSingle { response.events.map { it.message }.toDomain().toUi() }
                            .toObservable()
                            .map<ChatAction> { ChatAction.GetQueueMessage(action.queueId, lastId, action.topicName, it) }
                            .onErrorReturn { ChatInternalAction.LoadError(it) }
                    }
            }
    }
}