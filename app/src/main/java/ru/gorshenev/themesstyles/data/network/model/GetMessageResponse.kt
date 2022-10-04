package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMessageResponse(
    @SerialName("messages") val messages: List<MessageResponse>
)

@Serializable
data class MessageResponse(
    @SerialName("id") val msgId: Int,
    @SerialName("sender_full_name") val senderName: String,
    @SerialName("content") val content: String,
    @SerialName("sender_id") val senderId: Int,
    @SerialName("timestamp") val time: Long,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("reactions") val reactions: List<ReactionResponse>,
    @SerialName("subject") val subject: String
)

@Serializable
data class ReactionResponse(
    @SerialName("emoji_name") val emojiName: String,
    @SerialName("emoji_code") val emojiCode: String,
    @SerialName("reaction_type") val reactionType: String,
    @SerialName("user_id") val userId: Int,
)

@Serializable
data class GetOneMessageResponse(
    @SerialName("message") val message: MessageResponse
)
