package ru.gorshenev.themesstyles.presentation.ui.profile

import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseAction
import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseEffect

sealed class ProfileAction : BaseAction {
    object UploadProfile : ProfileAction()
}

sealed class ProfileInternalAction : ProfileAction() {
    object StartLoading : ProfileInternalAction()
    class LoadError(val error: Throwable) : ProfileInternalAction()
    class LoadResult(val profileName: String, val avatarUrl: String) :
        ProfileInternalAction()
}

sealed class ProfileEffect : BaseEffect {
    data class SnackBar(val error: Throwable) : ProfileEffect()
}


