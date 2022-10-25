package ru.gorshenev.themesstyles.mvi


data class UiState(
    val loading: Boolean = false,
    val data: Pair<String, String>? = null,
    val error: Throwable? = null
)

sealed class Action {
    object UploadProfile : Action()
}

sealed class InternalAction : Action() {
    object ProfileLoadingAction : InternalAction()
    class ProfileSuccessAction(val name: String, val avatar: String) : InternalAction()
    class ProfileFailureAction(val error: Throwable) : InternalAction()
}
