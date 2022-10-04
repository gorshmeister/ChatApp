package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTopicResponse(
    @SerialName("topics")
    val topics: List<TopicResponse>
)

@Serializable
data class TopicResponse(
    @SerialName("max_id") val maxId: Int,
    @SerialName("name") val name: String,
)