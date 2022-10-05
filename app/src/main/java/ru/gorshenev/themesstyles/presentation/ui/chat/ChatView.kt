package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.data.repositories.ChatRepository
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

interface ChatView {

    fun showItems(items: List<ViewTyped>)

    fun showError(error: Throwable?)

    fun showLoading()

    fun stopLoading()

    fun showToast()

    fun scrollMsgsToTheEnd()

}
