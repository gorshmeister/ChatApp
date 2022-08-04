package ru.gorshenev.themesstyles.hw3.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.ViewTyped

class ReactionsUi(
    val emojiCode: Int,
    override val id: Int = 0,
    override val viewType: Int = R.layout.item_emoji
) : ViewTyped {
}