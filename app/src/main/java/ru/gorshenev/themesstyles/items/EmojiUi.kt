package ru.gorshenev.themesstyles.items

data class EmojiUi(
    val code: Int,
    val counter: Int = 0,
    val isSelected: Boolean = false,
    val message_id: Int = 0,
    val user_id: List<Int> = emptyList()
)
