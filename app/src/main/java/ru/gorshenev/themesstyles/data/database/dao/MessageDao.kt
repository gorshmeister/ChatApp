package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Observable
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.MessageWithReactions
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity

@Dao
interface MessageDao {

    @Transaction
    @Query("SELECT * FROM message WHERE topicName in (:topic)")
    fun getMessages(topic: String): Observable<List<MessageWithReactions>>

    @Query("SELECT * FROM message WHERE topicName in (:topic)")
    fun getMessagesFromTopic(topic: String): Single<List<MessageEntity>>


    @Query("DELETE FROM reaction WHERE message_id in (:messageId)")
    fun deleteMessageReactions(messageId: Int)

    @Query("DELETE FROM message WHERE msgId in (:messageId)")
    fun deleteMessage(messageId: Int)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReactions(reactions: List<ReactionEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMessage(message: MessageEntity)


    @Transaction
    fun deleteFirstAndAddNewMessage(
        messageId: Int,
        messageEntity: MessageEntity,
        reactions: List<ReactionEntity>
    ) {
        deleteMessage(messageId = messageId)
        insertMessage(message = messageEntity)
        insertReactions(reactions = reactions)
    }

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