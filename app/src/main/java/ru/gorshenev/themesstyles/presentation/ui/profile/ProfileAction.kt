package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction

sealed class ProfileAction : BaseAction {
    object UploadProfile : ProfileAction()
}

sealed class ProfileInternalAction : ProfileAction() {
    object ProfileLoadingAction : ProfileInternalAction()
    class ProfileSuccessAction(val name: String, val avatar: String) : ProfileInternalAction()
    class ProfileFailureAction(val error: Throwable) : ProfileInternalAction()
}

