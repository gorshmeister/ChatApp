package ru.gorshenev.themesstyles.presentation.ui.chat.items

data class EmojiUi(
    val msgId: Int = 0,
    val name: String = "",
    val code: Int,
    val listUsersId: List<Int> = emptyList(),
    val counter: Int = 0,
    val isSelected: Boolean
)
