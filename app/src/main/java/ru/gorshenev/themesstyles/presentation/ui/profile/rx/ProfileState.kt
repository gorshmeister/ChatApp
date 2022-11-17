package ru.gorshenev.themesstyles.presentation.ui.profile.rx

import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseState

sealed class ProfileState : BaseState {
    object Loading : ProfileState()
    object Error : ProfileState()
    data class Result(val profileName: String, val avatarUrl: String) : ProfileState()
}