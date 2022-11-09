package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState
import java.util.concurrent.TimeUnit

class LoadMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.UploadMessages::class.java)
            .flatMap { action ->
                repository.getMessages(
                    streamName = action.streamName,
                    topicName = action.topicName,
                    anchorMessageId = ChatRepository.DEFAULT_MESSAGE_ANCHOR,
                    numBefore = ChatRepository.DEFAULT_NUM_BEFORE,
                    onlyRemote = false
                ).debounce(400, TimeUnit.MILLISECONDS)
                    .map<ChatAction> { ChatInternalAction.LoadResult(it.toUi()) }
                    .onErrorReturn { ChatInternalAction.LoadError(it) }
                    .startWithArray(
                        ChatAction.RegisterMessageQueue(action.streamName,action.topicName),
                        ChatAction.RegisterReactionQueue(action.streamName, action.topicName)
                    )
            }
    }
}