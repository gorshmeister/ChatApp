package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.repositories.StreamRepository
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

interface StreamView {

    fun showLoading()

    fun stopLoading()

    fun showItems(items: List<ViewTyped>)

    fun showError(error: Throwable?)

    fun goToChat(topic: TopicUi, stream: StreamUi)

    fun getDataBase() : AppDataBase

    fun repository(): StreamRepository

}
