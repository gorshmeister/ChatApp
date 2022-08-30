package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.ViewTyped

data class PeopleUi(
    override val id: Int,
    val name: String,
    val email: String = "abcdef@gmail.com",
    val avatar: Int = R.drawable.ic_launcher_background,
    override val viewType: Int = R.layout.item_people
): ViewTyped
