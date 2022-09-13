package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class DateUi(
    override val id: Int,
    val text: String,
    override val viewType: Int = R.layout.item_date
) : ViewTyped