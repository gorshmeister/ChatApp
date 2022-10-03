package ru.gorshenev.themesstyles.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.gorshenev.themesstyles.data.database.AppDataBase
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

@Entity(
    tableName = AppDataBase.TOPIC,
    foreignKeys = [ForeignKey(
        entity = StreamEntity::class,
        parentColumns = arrayOf("streamId", "strType"),
        childColumns = arrayOf("stream_id", "type"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Int = 0,
    @ColumnInfo(name = "stream_id") val streamId: Int,
    @ColumnInfo(name = "max_id") val maxId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "type") val type: StreamFragment.StreamType
)