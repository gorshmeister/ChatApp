package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.BaseView

interface PeopleView : BaseView {

//    fun showLoading()
//
//    fun stopLoading()
//
//    fun showError(error: Throwable?)

    fun showItems(items: List<ViewTyped>)
}