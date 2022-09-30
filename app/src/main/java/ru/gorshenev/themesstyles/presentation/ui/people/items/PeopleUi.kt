package ru.gorshenev.themesstyles.presentation.ui.people.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class PeopleUi(
    override val id: Int,
    val name: String,
    val email: String = "abcdef@gmail.com",
    val avatar: String = R.drawable.ic_launcher_background.toString(),
    val status: PeopleStatus = PeopleStatus.OFFLINE,
    override val viewType: Int = R.layout.item_people
) : ViewTyped {
    enum class PeopleStatus {
        ONLINE,
        IDLE,
        OFFLINE
    }
}

