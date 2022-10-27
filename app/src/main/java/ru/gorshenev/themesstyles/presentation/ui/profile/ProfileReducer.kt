package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.Reducer
import ru.gorshenev.themesstyles.presentation.mvi_core.UiEffects
import java.util.*

class ProfileReducer : Reducer<ProfileAction, ProfileState, UiEffects> {

    override fun reduceToState(action: ProfileAction, state: ProfileState): ProfileState {
        return when (action) {
            ProfileInternalAction.StartLoading -> ProfileState.Loading
            is ProfileInternalAction.LoadResult -> ProfileState.Result(
                profileName = action.profileName,
                avatarUrl = action.avatarUrl
            )
            is ProfileInternalAction.LoadError -> ProfileState.Error
            else -> state
        }
    }

    override fun reduceToEffect(action: ProfileAction, state: ProfileState): Optional<UiEffects> {
        return when (action) {
            is ProfileInternalAction.LoadError -> Optional.of(UiEffects.SnackBar(action.error))
            else -> Optional.empty()
        }
    }
}
