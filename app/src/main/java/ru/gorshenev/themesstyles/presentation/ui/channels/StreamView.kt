package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

interface StreamView {

    fun showLoading()

    fun stopLoading()

    fun showItems(items: List<ViewTyped>)

    fun showError(error: Throwable?)

    fun adapterItems(): List<ViewTyped>

}
