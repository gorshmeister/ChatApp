package ru.gorshenev.themesstyles.hw3.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.ViewTyped

data class TextUi(
    override val id: Int,
    val text: String,
    override val viewType: Int = R.layout.item_text
) : ViewTyped