package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer
import java.util.*

class ChatReducer : Reducer<ChatAction, ChatState, ChatEffect> {
    override fun reduceToState(action: ChatAction, state: ChatState): ChatState {
        return when (action) {
            ChatInternalAction.StartLoading -> {
                ChatState.Loading
            }
            is ChatInternalAction.LoadError, is ChatInternalAction.ReactionExist -> {
                ChatState.Error
            }
            is ChatInternalAction.LoadResult -> {
                ChatState.Result(action.items)
            }
            is ChatAction.GetQueueMessage -> {
                if (state is ChatState.Result && action.items.isNotEmpty())
                    ChatState.Result(state.items + action.items)
                else
                    state
            }
            is ChatAction.GetQueueReaction -> {
                if (action.items.isNotEmpty())
                    ChatState.Result(action.items)
                else
                    state
            }
            else -> state
        }
    }

    override fun reduceToEffect(action: ChatAction, state: ChatState): Optional<ChatEffect> {
        return when (action) {
            is ChatInternalAction.LoadError -> Optional.of(ChatEffect.SnackBar(action.error))
            is ChatInternalAction.ReactionExist -> Optional.of(ChatEffect.Toast(action.error))
            is ChatInternalAction.ScrollToTheEnd -> Optional.of(ChatEffect.Scroll)
            ChatInternalAction.EmptyAction -> Optional.empty()
            else -> Optional.empty()
        }
    }
}