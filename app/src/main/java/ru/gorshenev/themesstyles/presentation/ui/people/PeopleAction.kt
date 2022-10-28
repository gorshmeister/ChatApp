package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction

sealed class PeopleAction : BaseAction {
    object UploadUsers : PeopleAction()
    class SearchUsers(val items: List<ViewTyped>, val query: String) : PeopleAction()
}

sealed class PeopleInternalAction : PeopleAction() {
    object StartLoading : PeopleInternalAction()
    class LoadError(val error: Throwable) : PeopleInternalAction()
    class LoadResult(val items: List<ViewTyped>) : PeopleInternalAction()
    class LoadResultToCache(val items: List<ViewTyped>) : PeopleInternalAction()
}
