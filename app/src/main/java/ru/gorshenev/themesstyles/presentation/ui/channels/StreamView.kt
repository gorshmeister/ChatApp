package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.BaseView
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

interface StreamView : BaseView {

//    fun showLoading()

//    fun stopLoading()

//    fun showError(error: Throwable?)

    fun showItems(items: List<ViewTyped>)

    fun goToChat(topic: TopicUi, stream: StreamUi)

}
