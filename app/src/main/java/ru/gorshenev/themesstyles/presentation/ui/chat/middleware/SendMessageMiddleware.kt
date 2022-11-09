package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState
import javax.inject.Inject

class SendMessageMiddleware @Inject constructor(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.SendMessage::class.java)
            .flatMap { action ->
                repository.sendMessage(
                    messageText = action.messageText, streamName = action.streamName,
                    topicName = action.topicName
                ).toObservable()
                    .ofType(ChatAction::class.java)
                    .onErrorReturn { ChatInternalAction.LoadError(it) }
            }
    }
}