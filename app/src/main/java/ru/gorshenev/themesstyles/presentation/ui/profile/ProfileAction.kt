package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction

sealed class ProfileAction : BaseAction {
    object UploadProfile : ProfileAction()
}

sealed class ProfileInternalAction : ProfileAction() {
    object StartLoading : ProfileInternalAction()
    class DownloadFailure(val error: Throwable) : ProfileInternalAction()
    class DownloadSuccessful(val profileName: String, val avatarUrl: String) :
        ProfileInternalAction()
}

