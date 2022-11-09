package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseState

sealed class PeopleState : BaseState {
    object Loading : PeopleState()
    object Error : PeopleState()
    data class Result(val items: List<ViewTyped>, val visibleItems: List<ViewTyped>) : PeopleState()
}