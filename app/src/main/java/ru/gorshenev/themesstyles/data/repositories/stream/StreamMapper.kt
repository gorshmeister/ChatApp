package ru.gorshenev.themesstyles.data.repositories.stream

import ru.gorshenev.themesstyles.data.database.dao.StreamWithTopics
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.data.network.model.Stream
import ru.gorshenev.themesstyles.data.network.model.Topic
import ru.gorshenev.themesstyles.domain.model.StreamModel
import ru.gorshenev.themesstyles.domain.model.TopicModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

object StreamMapper {

    fun List<StreamWithTopics>.toDomain(): List<StreamModel> {
        return this.map { stream ->
            val topics = stream.topics
                .map { topicEntity ->
                    TopicModel(
                        id = topicEntity.maxId,
                        name = topicEntity.name,
                        color = topicEntity.color
                    )
                }

            StreamModel(
                id = stream.stream!!.streamId,
                name = stream.stream!!.name,
                topics = topics,
            )
        }
    }

    fun Stream.toDomain(topics: List<Topic>): StreamModel {
        return StreamModel(
            id = this.streamId,
            name = this.name,
            topics = topics.map { it.toDomain(this.color) }
        )
    }

    private fun Topic.toDomain(color: String): TopicModel {
        return TopicModel(
            id = this.maxId,
            name = this.name,
            color = color
        )
    }

    fun List<StreamModel>.toEntity(type: StreamFragment.StreamType): List<StreamEntity> {
        return this.map {
            StreamEntity(
                streamId = it.id,
                name = it.name,
                color = it.color,
                type = type
            )
        }
    }

    fun List<TopicModel>.toEntity(type: StreamFragment.StreamType, streamId: Int): List<TopicEntity> {
        return this.map {
            TopicEntity(
                streamId = streamId,
                name = it.name,
                maxId = it.id,
                color = it.color,
                type = type
            )
        }
    }

}