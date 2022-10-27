package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.BaseState

sealed class ProfileState : BaseState {
    object Loading : ProfileState()
    object Error : ProfileState()
    class Result(val profileName: String, val avatarUrl: String) : ProfileState()
}