package ru.gorshenev.themesstyles.data.repositories.stream

import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.StreamWithTopics
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.data.network.model.StreamResponse
import ru.gorshenev.themesstyles.data.network.model.TopicResponse
import ru.gorshenev.themesstyles.domain.model.channels.StreamModel
import ru.gorshenev.themesstyles.domain.model.channels.TopicModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

object StreamMapper {
    fun List<StreamWithTopics>.toDomain(): List<StreamModel> {
        return this.map { stream ->
            val topics = stream.topics
                .map { topicEntity ->
                    TopicModel(
                        id = topicEntity.maxId,
                        name = topicEntity.name,
                        color = topicEntity.color,
                        type = when {
                            topicEntity.color != "#2A9D8F" -> StreamFragment.StreamType.SUBSCRIBED
                            else -> StreamFragment.StreamType.ALL_STREAMS
                        }
                    )
                }

            StreamModel(
                id = stream.stream.streamId,
                name = stream.stream.name,
                topics = topics,
            )
        }
    }

    fun StreamResponse.toDomain(
        topicResponses: List<TopicResponse>
    ): StreamModel {
        return StreamModel(
            id = this.streamId,
            name = this.name,
            topics = topicResponses.map { it.toDomain(this.color) },
        )
    }

    fun TopicResponse.toDomain(color: String): TopicModel {
        return TopicModel(
            id = this.maxId,
            name = this.name,
            color = color,
            type = when {
                color != "#2A9D8F" -> StreamFragment.StreamType.SUBSCRIBED
                else -> StreamFragment.StreamType.ALL_STREAMS
            }
        )
    }

    fun List<StreamModel>.toEntity(type: StreamFragment.StreamType): List<StreamEntity> {
        return this.map { streamModel ->
            StreamEntity(
                streamId = streamModel.id,
                name = streamModel.name,
                color = streamModel.color,
                type = type
            )
        }
    }

    fun List<TopicModel>.toEntity(
        type: StreamFragment.StreamType,
        streamId: Int
    ): List<TopicEntity> {
        return this.map { topicModel ->
            TopicEntity(
                streamId = streamId,
                maxId = topicModel.id,
                name = topicModel.name,
                color = topicModel.color,
                type = type
            )
        }
    }

    @JvmName("toUiStreamModel")
    fun List<StreamModel>.toUi(strType: StreamFragment.StreamType): List<StreamUi> {
        return this.map { streamModel ->
            StreamUi(
                id = streamModel.id,
                name = streamModel.name,
                topics = streamModel.topics.toUi(strType),
                isExpanded = streamModel.isExpanded
            )
        }
    }

    private fun List<TopicModel>.toUi(strType: StreamFragment.StreamType): List<TopicUi> {
        return this.filter { it.type == strType }
            .map { topicModel ->
            TopicUi(
                id = topicModel.id,
                name = topicModel.name,
                color = topicModel.color
            )
        }
    }

}