package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped

data class MessageRightUi(
    override val id: Int,
    val text: String,
    val time: String,
    val emojis: List<EmojiUi>,
    override val viewType: Int = R.layout.view_message_right,
) : ViewTyped