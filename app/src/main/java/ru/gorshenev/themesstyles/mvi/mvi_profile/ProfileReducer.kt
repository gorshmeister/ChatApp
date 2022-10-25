package ru.gorshenev.themesstyles.mvi.mvi_profile

import ru.gorshenev.themesstyles.mvi.Action
import ru.gorshenev.themesstyles.mvi.InternalAction
import ru.gorshenev.themesstyles.mvi.Reducer
import ru.gorshenev.themesstyles.mvi.UiState

class ProfileReducer : Reducer<UiState, Action> {
    override fun reduce(state: UiState, action: Action): UiState {
        return when (action) {
            InternalAction.ProfileLoadingAction -> state.copy(
                loading = true,
                data = null,
                error = null
            )
            is InternalAction.ProfileSuccessAction -> state.copy(
                loading = false,
                data = action.name to action.avatar,
                error = null
            )
            is InternalAction.ProfileFailureAction -> state.copy(
                loading = false,
                data = null,
                error = action.error
            )
            Action.UploadProfile -> state
        }
    }
}
