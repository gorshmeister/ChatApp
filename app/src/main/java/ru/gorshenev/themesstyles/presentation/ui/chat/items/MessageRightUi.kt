package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class MessageRightUi(
    override val id: Int,
    val text: String,
    val time: String,
    val emojis: List<EmojiUi>,
    override val viewType: Int = R.layout.view_message_right,
) : ViewTyped