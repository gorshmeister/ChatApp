package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer

class ProfileReducer : Reducer<ProfileState, ProfileAction> {

    override fun reduce(state: ProfileState, action: ProfileAction): ProfileState {

        return when (action) {
            ProfileInternalAction.ProfileLoadingAction -> state.copy(
                isLoading = true,
                data = null,
                error = null
            )
            is ProfileInternalAction.ProfileSuccessAction -> state.copy(
                isLoading = false,
                data = Profile(action.name,action.avatar),
                error = null,
            )
            is ProfileInternalAction.ProfileFailureAction -> state.copy(
                isLoading = false,
                data = null,
                error = action.error,
            )
            ProfileAction.UploadProfile -> state
        }
    }
}
