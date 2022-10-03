package ru.gorshenev.themesstyles.data.database.entities

import androidx.room.*
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Entity(tableName = AppDataBase.STREAM, indices = [Index("streamId", "strType", unique = true)])
data class StreamEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Int = 0,
    @ColumnInfo(name = "streamId") val streamId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String = "#2A9D8F",
    @ColumnInfo(name = "strType") val type: StreamFragment.StreamType
)

data class StreamWithTopics(
    @Embedded
    var stream: StreamEntity,
    @Relation(parentColumn = "streamId", entityColumn = "stream_id")
    var topics: List<TopicEntity> = ArrayList()
)