package ru.gorshenev.themesstyles.fragments.people

import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped

interface PeopleView {

    fun showLoading()

    fun stopLoading()

    fun showError(error: Throwable?)

    fun showItems(items: List<ViewTyped>)

}