package ru.gorshenev.themesstyles.data.database.dao

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Single
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity

@Dao
interface MessageDao {

    @Transaction
    @Query("SELECT * FROM message WHERE topicName in (:topic)")
    fun getMessages(topic: String): Single<List<MessageWithReactions>>

    @Query("SELECT * FROM message WHERE topicName in (:topic)")
    fun getMessagesFromTopic(topic: String): Single<List<MessageEntity>>


    @Query("DELETE FROM reaction WHERE message_id in (:messageId)")
    fun deleteMessageReactions(messageId: Int): Completable

    @Query("DELETE FROM message WHERE msgId in (:messageId)")
    fun deleteMessage(messageId: Int): Completable


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(reactions: List<ReactionEntity>): Completable


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(message: MessageEntity): Completable

}