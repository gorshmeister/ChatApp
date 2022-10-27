package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer
import ru.gorshenev.themesstyles.presentation.mvi_core.UiEffects

class ProfileReducer : Reducer<ProfileAction, ProfileState, UiEffects> {

    override fun reduceToState(action: ProfileAction, state: ProfileState): ProfileState {
        return when (action) {
            ProfileInternalAction.StartLoading -> ProfileState.Loading
            is ProfileInternalAction.DownloadSuccessful -> ProfileState.Result(
                profileName = action.profileName,
                avatarUrl = action.avatarUrl
            )
            is ProfileInternalAction.DownloadFailure -> ProfileState.Error
            else -> state
        }
    }

    override fun reduceToEffect(action: ProfileAction, state: ProfileState): UiEffects? {
        return when (action) {
            is ProfileInternalAction.DownloadFailure -> UiEffects.SnackBar(action.error)
            else -> null
        }
    }
}
