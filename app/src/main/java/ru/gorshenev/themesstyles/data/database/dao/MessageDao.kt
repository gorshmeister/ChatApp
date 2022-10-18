package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.MessageWithReactionsEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity

@Dao
interface MessageDao {

    @Transaction
    @Query("SELECT * FROM message WHERE topicName in (:topicName)")
    fun getMessagesWithReactions(topicName: String): Single<List<MessageWithReactionsEntity>>

    @Query("SELECT * FROM message WHERE topicName in (:topicName)")
    fun getMessages(topicName: String): Single<List<MessageEntity>>


    @Query("DELETE FROM reaction WHERE message_id in (:messageId)")
    fun deleteMessageReactions(messageId: Int)

    @Query("DELETE FROM message WHERE msgId in (:messageId)")
    fun deleteMessage(messageId: Int)

    @Query("DELETE FROM message WHERE topicName in (:topicName)")
    fun deleteMessages(topicName: String)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReactions(reactions: List<ReactionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessages(messages: List<MessageEntity>)

    @Transaction
    fun insertMessageWithReactions(
        messageEntity: MessageEntity,
        reactions: List<ReactionEntity>
    ){
        insertMessage(messageEntity)
        insertReactions(reactions)
    }

    @Transaction
    fun updateMessageReactions(
        messageId: Int,
        reactions: List<ReactionEntity>
    ){
        deleteMessageReactions(messageId)
        insertReactions(reactions)
    }

}