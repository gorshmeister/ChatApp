package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class ReactionsUi(
    val emojiCode: Int,
    override val id: Int = 0,
    override val viewType: Int = R.layout.item_emoji
) : ViewTyped