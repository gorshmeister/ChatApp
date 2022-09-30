package ru.gorshenev.themesstyles.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import ru.gorshenev.themesstyles.data.network.model.Reaction

@Entity(tableName = "message")
data class MessageEntity(
    @PrimaryKey val msgId: Int,
    @ColumnInfo(name = "topicName") val topicName: String ,
    @ColumnInfo(name = "sender_full_name") val senderName: String ,
    @ColumnInfo(name = "content") val content: String ,
    @ColumnInfo(name = "sender_id") val senderId: Int ,
    @ColumnInfo(name = "timestamp") val time: Long ,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String? ,
    @ColumnInfo(name = "subject") val subject: String
)