package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.mvi_core.Reducer
import java.util.*
import javax.inject.Inject

class ChatReducer @Inject constructor(): Reducer<ChatAction, ChatState, ChatEffect> {
    override fun reduceToState(action: ChatAction, state: ChatState): ChatState {
        return when (action) {
            ChatInternalAction.StartLoading -> {
                ChatState.Loading
            }
            ChatInternalAction.StartPaginationLoading -> {
                if (state is ChatState.Result) {
                    state.copy(isPaginationLoading = true)
                } else {
                    state
                }
            }
            is ChatInternalAction.LoadResult -> {
                ChatState.Result(action.items, false)
            }
            is ChatInternalAction.LoadError -> {
                ChatState.Error
            }
            is ChatAction.GetQueueMessage -> {
                if (state is ChatState.Result && action.items.isNotEmpty()) {
                    state.copy(items = state.items + action.items)
                } else {
                    state
                }
            }
            is ChatAction.GetQueueReaction -> {
                if (state is ChatState.Result && action.items.isNotEmpty()) {
                    state.copy(items = action.items)
                } else {
                    state
                }
            }
            else -> state
        }
    }

    override fun reduceToEffect(action: ChatAction, state: ChatState): Optional<ChatEffect> {
        return when (action) {
            is ChatInternalAction.LoadError -> Optional.of(ChatEffect.SnackBar(action.error))
            is ChatInternalAction.StartPaginationLoading -> Optional.of(ChatEffect.ProgressBar)
            is ChatInternalAction.ScrollToTheEnd -> Optional.of(ChatEffect.Scroll)
            else -> Optional.empty()
        }
    }
}