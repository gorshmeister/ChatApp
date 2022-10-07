package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserPresenceResponse(
    @SerialName("presence") val presence: Presence
)

@Serializable
data class Presence(
    @SerialName("aggregated") val aggregated: Aggregated
)

@Serializable
data class Aggregated(
    @SerialName("status") val status: PeopleStatusResponse,
    @SerialName("timestamp") val time: Long
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
