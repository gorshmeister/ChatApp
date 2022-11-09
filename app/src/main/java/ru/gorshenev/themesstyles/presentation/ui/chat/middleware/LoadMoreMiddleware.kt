package ru.gorshenev.themesstyles.presentation.ui.chat.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toUi
import ru.gorshenev.themesstyles.data.repositories.chat.ChatRepository
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatInternalAction
import ru.gorshenev.themesstyles.presentation.ui.chat.ChatState

class LoadMoreMiddleware(private val repository: ChatRepository) :
    Middleware<ChatAction, ChatState> {
    override fun bind(
        actions: Observable<ChatAction>,
        state: Observable<ChatState>
    ): Observable<ChatAction> {
        return actions.ofType(ChatAction.UploadMoreMessages::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .filter { (_, state) -> state is ChatState.Result && !state.isPaginationLoading }
            .flatMap { (action, state) ->
                val displayedItems = (state as ChatState.Result).items
                val loadResultAction = repository.getMessages(
                    streamName = action.streamName,
                    topicName = action.topicName,
                    anchorMessageId = displayedItems.first().id.toLong(),
                    numBefore = ChatRepository.MORE_NUM_BEFORE,
                    onlyRemote = true
                )
                    .map<ChatAction> { models ->
                        ChatInternalAction.LoadResult((models.toUi() + displayedItems)
                            .distinctBy { it.id }
                            .sortedBy { it.id })
                    }

                val showPaginationLoadingAction =
                    Observable.just(ChatInternalAction.StartPaginationLoading)

                Observable.mergeDelayError(showPaginationLoadingAction, loadResultAction)
            }.onErrorReturn { ChatInternalAction.LoadError(it) }
    }
}