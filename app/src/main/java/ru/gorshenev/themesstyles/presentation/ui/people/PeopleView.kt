package ru.gorshenev.themesstyles.presentation.ui.people

import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import java.lang.Error

interface PeopleView {

    fun showLoading()

    fun stopLoading()

    fun showError(error: Throwable?)

    fun showItems(items: List<ViewTyped>)
}