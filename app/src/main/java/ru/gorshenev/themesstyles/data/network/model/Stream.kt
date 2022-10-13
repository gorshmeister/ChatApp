package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import ru.gorshenev.themesstyles.R

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
    @SerialName("color") val color: String = R.color.color_primary.toString()
)