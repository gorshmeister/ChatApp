package ru.gorshenev.themesstyles.data.repositories.chat

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.gorshenev.themesstyles.data.database.dao.MessageDao
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.*
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.chat.MessageModel

class ChatRepository(private val messageDao: MessageDao, private val api: ZulipApi) {

    private lateinit var streamName: String
    private lateinit var topicName: String

    fun getMessagesFromDb(strName: String, tpcName: String): Observable<List<MessageModel>> {
        streamName = strName
        topicName = tpcName
        return messageDao.getMessages(topicName)
            .map { messageWithReactions -> messageWithReactions.toDomain() }
            .subscribeOn(Schedulers.io())
    }

    fun getMessagesFromApi(): Observable<GetMessageResponse> {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )
        return api.getMessages(
            anchor = 10000000000000000,
            numBefore = 50,
            numAfter = 0,
            narrow = narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
    }

    fun uploadMoreMessages(firstMessageIdInDb: Long): Observable<List<MessageModel>> {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )
        return api.getMessages(
            firstMessageIdInDb,
            20,
            0,
            narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .map { messageResponse -> messageResponse.messages.toDomain() }
    }

    fun registerMessageQueue(): Observable<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("message"))

        return api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
    }

    fun getQueueMessages(
        currentMessageQueueId: String,
        lastId: Int
    ): Observable<GetMessageEventsResponse> {
        return api.getEventsFromQueue(currentMessageQueueId, lastId)
    }

    fun registerReactionQueue(): Observable<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("reaction"))

        return api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
    }

    fun getQueueReactions(queueId: String, lastId: Int): Observable<GetEmojiEventsResponse> {
        return api.getEmojiEventsFromQueue(queueId, lastId)
    }

    fun getMessage(messageId: Int) = api.getMessage(messageId)


    fun onEmojiClick(emojiName: String, messageId: Int): Observable<CreateReactionResponse> {
        return api.getMessage(id = messageId, applyMarkdown = true)
            .map { response ->
                response.message.reactions.filter { reaction -> reaction.emojiName == emojiName }
                    .any { it.userId == Reactions.MY_USER_ID }
            }
            .concatMapSingle { isMyClick -> updateEmoji(messageId, emojiName, isMyClick) }
            .subscribeOn(Schedulers.io())
    }

    private fun updateEmoji(
        msgId: Int,
        emojiName: String,
        isMyClick: Boolean = false
    ): Single<CreateReactionResponse> {
        return when {
            isMyClick -> api.deleteEmoji(msgId = msgId, emojiName)
            else -> api.addEmoji(msgId = msgId, emojiName)
        }
    }

    fun sendMessage(message: String): Single<CreateMessageResponse> {
        return api.sendMessage(
            to = streamName,
            topic = topicName,
            content = message
        )
            .subscribeOn(Schedulers.io())
    }

    private fun updateDb(
        messagesInDb: List<MessageEntity>, newMessage: MessageResponse, topicName: String
    ): Completable {
        val msgDoesNotExist = messagesInDb.all { it.msgId != newMessage.msgId }
        val msgExist = messagesInDb.any { it.msgId == newMessage.msgId }

        return when {
            messagesInDb.isEmpty() || msgDoesNotExist && messagesInDb.size != 50 -> {
                Completable.fromCallable {
                    messageDao.insertMessageWithReactions(
                        newMessage.toEntity(topicName),
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
            }

            msgExist -> {
                Completable.fromCallable {
                    messageDao.updateMessageReactions(
                        newMessage.msgId,
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
            }

            msgDoesNotExist && messagesInDb.size == 50 -> {
                Completable.fromCallable {
                    messageDao.deleteFirstAndAddNewMessage(
                        messagesInDb.first().msgId,
                        newMessage.toEntity(topicName),
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
            }

            else -> {
                Log.d("database", "add to data ERROR")
                Completable.error(Error("add to data ERROR"))
            }
        }
    }


    fun addToDatabase(
        newMessage: MessageResponse, topicName: String
    ): Completable {
        return messageDao.getMessagesFromTopic(topicName)
            .flatMapCompletable { messageEntityList ->
                updateDb(messageEntityList, newMessage, topicName)
            }
    }

}