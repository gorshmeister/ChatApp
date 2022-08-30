package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.ViewTyped

data class DateUi(
    override val id: Int,
    val text: String,
    override val viewType: Int = R.layout.item_date
) : ViewTyped