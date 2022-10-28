package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer
import ru.gorshenev.themesstyles.presentation.mvi_core.UiEffects
import java.util.*

class PeopleReducer : Reducer<PeopleAction, PeopleState, UiEffects> {
    override fun reduceToState(action: PeopleAction, state: PeopleState): PeopleState {
        return when (action) {
            PeopleInternalAction.StartLoading -> PeopleState.Loading
            is PeopleInternalAction.LoadError -> PeopleState.Error
            is PeopleInternalAction.LoadResult -> PeopleState.Result(action.items)
            is PeopleInternalAction.LoadResultToCache -> PeopleState.ResultWithCache(action.items)
            else ->state
        }
    }

    override fun reduceToEffect(action: PeopleAction, state: PeopleState): Optional<UiEffects> {
        return when (action) {
            is PeopleInternalAction.LoadError -> Optional.of(UiEffects.SnackBar(action.error))
            else -> Optional.empty()
        }
    }
}