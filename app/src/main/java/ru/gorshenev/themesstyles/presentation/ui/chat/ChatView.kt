package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.BaseView
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

interface ChatView : BaseView {

    fun showItems(items: List<ViewTyped>)

    fun showReactionExistsToast()

    fun scrollToTheEnd()

}
