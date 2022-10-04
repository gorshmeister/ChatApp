package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetEmojiEventsResponse(
    @SerialName("events") val events: List<EmojiEvent>,
)

@Serializable
data class EmojiEvent(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: String,
    @SerialName("op") val addOrRemove: String,
    @SerialName("message_id") val messageId: Int,
    @SerialName("emoji_name") val emojiName: String,
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("user_id") val userId: Int,
)