package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.mvi_core.Reducer
import java.util.*

class PeopleReducer : Reducer<PeopleAction, PeopleState, PeopleEffect> {
    override fun reduceToState(action: PeopleAction, state: PeopleState): PeopleState {
        return when (action) {
            PeopleInternalAction.StartLoading -> {
                PeopleState.Loading
            }
            is PeopleInternalAction.LoadError -> {
                PeopleState.Error
            }
            is PeopleInternalAction.LoadResult -> {
                PeopleState.Result(action.items, action.items)
            }
            is PeopleInternalAction.SearchResult -> {
                if (state is PeopleState.Result) {
                    state.copy(visibleItems = action.items)
                } else {
                    state
                }
            }
            else -> state
        }
    }

    override fun reduceToEffect(action: PeopleAction, state: PeopleState): Optional<PeopleEffect> {
        return when (action) {
            is PeopleInternalAction.LoadError -> Optional.of(PeopleEffect.SnackBar(action.error))
            else -> Optional.empty()
        }
    }
}