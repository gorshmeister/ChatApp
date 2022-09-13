package ru.gorshenev.themesstyles.presentation.ui.chat.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class MessageLeftUi(
    override val id: Int,
    val avatar: String? = R.drawable.ic_launcher_background.toString(),
    val name: String,
    val text: String,
    val time: String ,
    val emojis: List<EmojiUi>,
    override val viewType: Int = R.layout.view_message_left,
) : ViewTyped
