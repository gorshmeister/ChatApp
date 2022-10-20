package ru.gorshenev.themesstyles.domain.model.chat

data class MessageModel(
    val id: Int,
    val avatar: String? = null,
    val name: String = "",
    val text: String,
    val time: String,
    val emojis: List<EmojiModel>,
    val myMessage: Boolean = false
)
