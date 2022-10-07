package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class GetStreamResponse(
    @SerialName("streams")
    @JsonNames("subscriptions")
    val streams: List<StreamResponse>
)

@Serializable
data class StreamResponse(
    @SerialName("stream_id") val streamId: Int,
    @SerialName("name") val name: String,
    @SerialName("color") val color: String = "#2A9D8F"
)