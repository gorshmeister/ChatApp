package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction

sealed class ProfileAction : BaseAction {
    object UploadProfile : ProfileAction()
}

sealed class ProfileInternalAction : ProfileAction() {
    object StartLoading : ProfileInternalAction()
    class LoadError(val error: Throwable) : ProfileInternalAction()
    class LoadResult(val profileName: String, val avatarUrl: String) :
        ProfileInternalAction()
}

