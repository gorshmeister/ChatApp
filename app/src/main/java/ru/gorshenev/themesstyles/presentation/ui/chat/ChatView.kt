package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.BaseView

interface ChatView : BaseView {

//    fun showError(error: Throwable?)
//
//    fun showLoading()
//
//    fun stopLoading()

    fun showItems(items: List<ViewTyped>)

    fun showToast()

    fun scrollToTheEnd()

}
