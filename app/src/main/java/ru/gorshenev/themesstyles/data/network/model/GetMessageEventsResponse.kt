package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetMessageEventsResponse(
    @SerialName("events") val events: List<Event>,
)

@Serializable
data class Event(
    @SerialName("id") val id: Int,
    @SerialName("type") val type: String,
    @SerialName("message") val message: MessageResponse,
)