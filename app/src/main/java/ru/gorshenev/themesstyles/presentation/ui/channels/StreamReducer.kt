package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base.mvi_core.Reducer
import java.util.*

class StreamReducer : Reducer<StreamAction, StreamState, StreamEffect> {
    override fun reduceToState(action: StreamAction, state: StreamState): StreamState {
        return when (action) {
            StreamInternalAction.StartLoading -> {
                StreamState.Loading
            }
            is StreamInternalAction.LoadError -> {
                StreamState.Error
            }
            is StreamInternalAction.LoadResult -> {
                StreamState.Result(action.items, action.items)
            }
            is StreamInternalAction.SearchResult -> {
                if (state is StreamState.Result) {
                    state.copy(visibleItems = action.items)
                } else {
                    state
                }
            }
            is StreamInternalAction.StreamExpandedResult -> {
                if (state is StreamState.Result) {
                    state.copy(visibleItems = action.items)
                } else {
                    state
                }
            }
            else -> state
        }
    }

    override fun reduceToEffect(action: StreamAction, state: StreamState): Optional<StreamEffect> {
        return when (action) {
            is StreamInternalAction.LoadError -> Optional.of(StreamEffect.SnackBar(action.error))
            is StreamInternalAction.OpenChat -> Optional.of(
                StreamEffect.OpenChat(action.topicName, action.streamName)
            )
            else -> Optional.empty()
        }
    }
}