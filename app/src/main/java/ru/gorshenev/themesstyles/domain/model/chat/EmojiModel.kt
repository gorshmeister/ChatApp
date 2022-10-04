package ru.gorshenev.themesstyles.domain.model.chat

data class EmojiModel(
    val msgId: Int = 0,
    val name: String = "",
    val code: Int,
    val listUsersId: List<Int> = emptyList(),
    val counter: Int = 0,
    val isSelected: Boolean = false
)
