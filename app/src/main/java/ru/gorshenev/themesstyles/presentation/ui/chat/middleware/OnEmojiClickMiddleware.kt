package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState

class OnEmojiClickMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.OnEmojiClick::class.java)
            .flatMap { action ->
                repository.updateEmoji(
                    emojiName = action.emojiName,
                    messageId = action.messageId,
                    throwOnConflict = action.isBottomSheetClick
                )
                    .toObservable()
                    .map<ChatAction> { ChatInternalAction.EmptyAction }
                    .onErrorReturn { err ->
                        when (err) {
                            is Errors.ReactionAlreadyExist -> ChatInternalAction.ReactionExist(err)
                            else -> ChatInternalAction.LoadError(err)
                        }
                    }
            }
    }
}