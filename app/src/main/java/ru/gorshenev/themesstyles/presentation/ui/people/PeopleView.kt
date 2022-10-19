package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.BaseView
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

interface PeopleView : BaseView {

    fun showItems(items: List<ViewTyped>)
}