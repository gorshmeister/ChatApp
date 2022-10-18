package ru.gorshenev.themesstyles.presentation.ui.people.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

data class PeopleUi(
    override val id: Int,
    val name: String,
    val email: String,
    val avatar: String,
    val status: PeopleStatus = PeopleStatus.OFFLINE,
    override val viewType: Int = R.layout.item_people
) : ViewTyped {
    enum class PeopleStatus {
        ONLINE,
        IDLE,
        OFFLINE
    }
}

