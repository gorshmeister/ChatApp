package ru.gorshenev.themesstyles.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
@Entity(
    tableName = "reaction",
    foreignKeys = [ForeignKey(
        entity = MessageEntity::class,
        parentColumns = arrayOf("msgId"),
        childColumns = arrayOf("message_id"),
        onDelete = CASCADE
    )]
)
data class ReactionEntity(
    @PrimaryKey(autoGenerate = true) val primaryKey: Int = 0,
    @ColumnInfo(name = "message_id") val messageId: Int,
    @ColumnInfo(name = "emoji_name") val emojiName: String,
    @ColumnInfo(name = "emoji_code") val emojiCode: String,
    @ColumnInfo(name = "reaction_type") val reactionType: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "topic_name") val topicName: String,
)