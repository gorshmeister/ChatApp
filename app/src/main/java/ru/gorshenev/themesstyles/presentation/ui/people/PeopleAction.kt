package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseEffect

sealed class PeopleAction : BaseAction {
    object UploadUsers : PeopleAction()
    data class SearchUsers(val items: List<ViewTyped>, val query: String) : PeopleAction()
}

sealed class PeopleInternalAction : PeopleAction() {
    object StartLoading : PeopleInternalAction()
    data class LoadError(val error: Throwable) : PeopleInternalAction()
    data class LoadResult(val items: List<ViewTyped>) : PeopleInternalAction()
    data class SearchResult(val items: List<ViewTyped>) : PeopleInternalAction()
}

sealed class PeopleEffect : BaseEffect {
    data class SnackBar(val error: Throwable) : PeopleEffect()
}