package ru.gorshenev.themesstyles.data.repositories.stream

import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.StreamWithTopicsEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity
import ru.gorshenev.themesstyles.data.network.model.StreamResponse
import ru.gorshenev.themesstyles.data.network.model.TopicResponse
import ru.gorshenev.themesstyles.domain.model.channels.StreamModel
import ru.gorshenev.themesstyles.domain.model.channels.TopicModel
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

object StreamMapper {
    @JvmName("toDomainStreamWithTopics")
    fun List<StreamWithTopicsEntity>.toDomain(): List<StreamModel> {
        return this.map { streamWithTopics ->
            StreamModel(
                id = streamWithTopics.stream.streamId,
                name = streamWithTopics.stream.name,
                topics = streamWithTopics.topics.toDomain()
            )
        }
    }

    private fun List<TopicEntity>.toDomain(): List<TopicModel> {
        return this.map { topic ->
            TopicModel(
                id = topic.maxId,
                name = topic.name,
                color = topic.color,
                type = topic.type
            )
        }
    }


    fun StreamResponse.toDomain(
        topicResponse: List<TopicResponse>,
        type: StreamFragment.StreamType
    ): StreamModel {
        return StreamModel(
            id = this.streamId,
            name = this.name,
            topics = topicResponse.map { it.toDomain(this.color, type) }
        )
    }

    fun TopicResponse.toDomain(color: String, type: StreamFragment.StreamType): TopicModel {
        return TopicModel(
            id = this.maxId,
            name = this.name,
            color = color,
            type = type
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
                isExpanded = false
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