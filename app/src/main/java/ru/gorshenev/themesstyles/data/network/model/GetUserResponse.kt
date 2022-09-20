package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames


@Serializable
data class GetUserPresence(
    @SerialName("presence")
    val presence: Presence
)

@Serializable
data class Presence(
    @SerialName("aggregated")
    val aggregated: Aggregated
)

@Serializable
data class Aggregated(
    @SerialName("status")
    val status: PeopleStatusResponse,
    @SerialName("timestamp")
    val time: Long
)

@Serializable
enum class PeopleStatusResponse {
    @SerialName("active")
    ONLINE,

    @SerialName("idle")
    IDLE,

    @SerialName("offline")
    OFFLINE
}


@Serializable
data class GetOneUserResponse(
    @SerialName("user")
    val members: User
)

@Serializable
data class GetUserResponse(
    @SerialName("members")
    val members: List<User>
)

@Serializable
data class User(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("full_name")
    val firstName: String,
    @SerialName("delivery_email")
    val email: String,
    @SerialName("timezone")
    val timeZone: String,
    @SerialName("avatar_url")
    val avatarUrl: String
)


@Serializable
data class GetStreamResponse(
    @JsonNames("subscriptions", "streams")
    val streams: List<Stream>,
)

@Serializable
data class Stream(
    @SerialName("stream_id")
    val streamId: Int,
    @SerialName("name")
    val name: String,
    @SerialName("color")
    val color: String = "#2A9D8F"
)


@Serializable
data class GetTopicResponse(
    @SerialName("topics")
    val topics: List<Topic>
)

@Serializable
data class Topic(
    @SerialName("max_id")
    val maxId: Int,
    @SerialName("name")
    val name: String,
)


@Serializable
data class CreateMessageResponse(
    @SerialName("id")
    val id: Int
)

@Serializable
data class CreateReactionResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
)


@Serializable
data class GetOneMessageResponse(
    @SerialName("message")
    val message: Message
)

@Serializable
data class GetMessageResponse(
    @SerialName("messages")
    val messages: List<Message>
)

@Serializable
data class Message(
    @SerialName("id")
    val msgId: Int,
    @SerialName("sender_full_name")
    val senderName: String,
    @SerialName("content")
    val content: String,
    @SerialName("sender_id")
    val senderId: Int,
    @SerialName("timestamp")
    val time: Long,
    @SerialName("avatar_url")
    val avatarUrl: String?,
    @SerialName("reactions")
    val reactions: List<Reaction>,
    @SerialName("subject")
    val subject: String
)

@Serializable
data class Reaction(
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("reaction_type")
    val reactionType: String,
    @SerialName("user_id")
    val userId: Int,
)


@Serializable
data class CreateQueueResponse(
    @SerialName("result")
    val result: String,
    @SerialName("msg")
    val msg: String,
    @SerialName("queue_id")
    val queueId: String,
    @SerialName("last_event_id")
    val lastId: Int
)

@Serializable
data class GetEmojiEventsResponse(
    @SerialName("events")
    val events: List<EmojiEvent>,
)

@Serializable
data class EmojiEvent(
    @SerialName("id")
    val id: Int,
    @SerialName("type")
    val type: String,
    @SerialName("op")
    val addOrRemove: ReactionAddOrRemove,
    @SerialName("message_id")
    val messageId: Int,
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("user_id")
    val userId: Int,
)

@Serializable
enum class ReactionAddOrRemove {
    @SerialName("add")
    ADD,

    @SerialName("remove")
    REMOVE
}



@Serializable
data class GetEventsResponse(
    @SerialName("events")
    val events: List<Event>,
)

@Serializable
data class Event(
    @SerialName("id")
    val id: Int,
    @SerialName("type")
    val type: String,
    @SerialName("message")
    val message: Message,
)


@Serializable
data class Narrow(
    val operator: String,
    val operand: String
)
