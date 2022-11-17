package ru.gorshenev.themesstyles.presentation.ui.profile.coroutines

sealed class ProfileState {
    object Loading : ProfileState()
    object Error : ProfileState()
    data class Result(val profileName: String, val avatarUrl: String) : ProfileState()
}

sealed class ProfileAction {
    object LoadProfile : ProfileAction()
}

sealed class ProfileChange {
    object StartLoading : ProfileChange()
    class LoadError(val error: Throwable) : ProfileChange()
    class LoadResult(val profileName: String, val avatarUrl: String) : ProfileChange()
}

sealed class ProfileEffect {
    data class SnackBar(val error: Throwable) : ProfileEffect()
}

