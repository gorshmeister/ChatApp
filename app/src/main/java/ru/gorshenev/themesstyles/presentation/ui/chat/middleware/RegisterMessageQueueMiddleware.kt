package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatFragment
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState

class RegisterMessageQueueMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.RegisterMessageQueue::class.java)
            .flatMap { action ->
                repository.registerMessageQueue(action.streamName, action.topicName)
                    .toObservable()
                    .map<ChatAction> {
                        ChatAction.GetQueueMessage(
                            queueId = it.queueId,
                            lastId = ChatFragment.LAST_ID,
                            topicName = action.topicName,
                            items = emptyList()
                        )
                    }.onErrorReturn { ChatInternalAction.LoadError(it) }
            }
    }
}
