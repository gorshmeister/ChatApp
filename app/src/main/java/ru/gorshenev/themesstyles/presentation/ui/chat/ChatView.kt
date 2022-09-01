package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

interface ChatView {

    fun showItems(items: List<ViewTyped>)

    fun showError(error: Throwable?)

    fun showToast()

}