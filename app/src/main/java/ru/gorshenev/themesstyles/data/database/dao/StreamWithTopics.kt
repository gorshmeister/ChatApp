package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.Embedded
import androidx.room.Relation
import ru.gorshenev.themesstyles.data.database.entities.StreamEntity
import ru.gorshenev.themesstyles.data.database.entities.TopicEntity

class StreamWithTopics {
    @Embedded
    var stream: StreamEntity? = null

    @Relation(parentColumn = "streamId", entityColumn = "stream_id")
    var topics : List<TopicEntity> = ArrayList()
}
