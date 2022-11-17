package ru.gorshenev.themesstyles.presentation.ui.profile.rx

import ru.gorshenev.themesstyles.presentation.base.mvi_core.Reducer
import java.util.*
import javax.inject.Inject

class ProfileReducer @Inject constructor() : Reducer<ProfileAction, ProfileState, ProfileEffect> {

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

    override fun reduceToEffect(action: ProfileAction, state: ProfileState): Optional<ProfileEffect> {
        return when (action) {
            is ProfileInternalAction.LoadError -> Optional.of(ProfileEffect.SnackBar(action.error))
            else -> Optional.empty()
        }
    }
}
