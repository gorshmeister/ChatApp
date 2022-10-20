package ru.gorshenev.themesstyles.domain.model.chat

data class EmojiModel(
    val msgId: Int,
    val name: String,
    val code: Int,
    val listUsersId: List<Int>
)
