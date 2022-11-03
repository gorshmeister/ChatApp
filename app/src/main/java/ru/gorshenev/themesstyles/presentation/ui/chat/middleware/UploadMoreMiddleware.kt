package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState

class UploadMoreMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.UploadMoreMessages::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .flatMap { (action, state) ->
                val displayedItems = if (state is ChatState.Result) state.items else emptyList()
                repository.getMessages(
                    streamName = action.streamName,
                    topicName = action.topicName,
                    anchorMessageId = displayedItems.first().id.toLong(),
                    ChatRepository.MORE_NUM_BEFORE,
                    onlyRemote = true
                )
                    //flag isLoading?
                    .map<ChatAction> { models ->
                        ChatInternalAction.LoadResult((models.toUi() + displayedItems)
                            .distinctBy { it.id }
                            .sortedBy { it.id })
                    }
                    .onErrorReturn { ChatInternalAction.LoadError(it) }
            }
    }
}