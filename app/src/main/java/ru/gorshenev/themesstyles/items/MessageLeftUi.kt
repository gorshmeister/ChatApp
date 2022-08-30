package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped

data class MessageLeftUi(
    override val id: Int,
    val avatar: Int = R.drawable.ic_launcher_background,
    val name: String,
    val text: String,
    val time: String,
    val emojis: List<EmojiUi>,
    override val viewType: Int = R.layout.view_message_left,
) : ViewTyped
