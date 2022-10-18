package ru.gorshenev.themesstyles.data.database.entities

import androidx.annotation.ColorInt
import androidx.room.*
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Entity(tableName = AppDataBase.STREAM, indices = [Index("streamId", "strType", unique = true)])
data class StreamEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Int = 0,
    @ColumnInfo(name = "streamId") val streamId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") @ColorInt val color: Int = 0,
    @ColumnInfo(name = "strType") val type: StreamFragment.StreamType
)

data class StreamWithTopicsEntity(
    @Embedded
    val stream: StreamEntity,
    @Relation(parentColumn = "streamId", entityColumn = "stream_id")
    val topics: List<TopicEntity> = ArrayList()
)