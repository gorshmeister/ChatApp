package ru.gorshenev.themesstyles.data.database.entities

import androidx.room.*
import ru.gorshenev.themesstyles.data.database.AppDataBase

@Entity(tableName = AppDataBase.MESSAGE)
data class MessageEntity(
    @PrimaryKey val msgId: Int,
    @ColumnInfo(name = "topicName") val topicName: String,
    @ColumnInfo(name = "sender_full_name") val senderName: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "sender_id") val senderId: Int,
    @ColumnInfo(name = "timestamp") val time: Long,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "subject") val subject: String
)

data class MessageWithReactions(
    @Embedded
    val message: MessageEntity,
    @Relation(parentColumn = "msgId", entityColumn = "message_id")
    val reactions: List<ReactionEntity> = ArrayList()
)
