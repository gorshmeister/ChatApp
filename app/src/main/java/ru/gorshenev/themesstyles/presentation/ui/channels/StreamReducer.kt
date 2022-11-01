package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer
import ru.gorshenev.themesstyles.presentation.mvi_core.UiEffects
import java.util.*

class StreamReducer : Reducer<StreamAction, StreamState, UiEffects> {
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
            is StreamInternalAction.OpenChat -> {
                StreamState.OpenChat(action.topic, action.stream)
            }
            is StreamInternalAction.SearchResult -> {
                if (state is StreamState.Result) {
                    state.copy(visibleItems = action.items)
                } else {
                    state
                }
            }
            is StreamInternalAction.ExpandStream -> {
                if (state is StreamState.Result) {
                    state.copy( visibleItems = action.items)
                } else {
                    state
                }
            }
            else -> state
        }
    }

    override fun reduceToEffect(action: StreamAction, state: StreamState): Optional<UiEffects> {
        return when (action) {
            is StreamInternalAction.LoadError -> Optional.of(UiEffects.SnackBar(action.error))
            else -> Optional.empty()
        }
    }
}