package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

data class ReactionsUi(
    val category: String,
    val name: String,
    val code: String,
    override val id: Int = 0,
    override val viewType: Int = R.layout.item_emoji
) : ViewTyped