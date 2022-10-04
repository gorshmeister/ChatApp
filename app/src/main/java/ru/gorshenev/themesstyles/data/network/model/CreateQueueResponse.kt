package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateQueueResponse(
    @SerialName("result") val result: String,
    @SerialName("msg") val msg: String,
    @SerialName("queue_id") val queueId: String,
    @SerialName("last_event_id") val lastId: Int
)

@Serializable
data class CreateMessageResponse(
    @SerialName("id") val id: Int
)

@Serializable
data class CreateReactionResponse(
    @SerialName("result") val result: String,
    @SerialName("msg") val msg: String,
)