package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.BaseState

class Profile(val name: String, val avatar: String)

data class ProfileState(
    val isLoading: Boolean = false,
    val data: Profile? = null,
    val error: Throwable? = null,
) : BaseState