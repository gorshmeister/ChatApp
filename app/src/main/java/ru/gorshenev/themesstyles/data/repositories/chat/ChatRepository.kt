package ru.gorshenev.themesstyles.data.repositories.chat

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


    fun getMessagesFromDb(topicName: String): Single<List<MessageModel>> {
        return messageDao.getMessages(topicName)
            .map { messageWithReactions -> messageWithReactions.toDomain() }
            .subscribeOn(Schedulers.io())
    }

    fun getMessagesFromApi(streamName: String, topicName: String): Single<GetMessageResponse> {
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

    fun uploadMoreMessages(
        firstMessageId: Long,
        streamName: String,
        topicName: String
    ): Single<List<MessageModel>> {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )
        return api.getMessages(
            firstMessageId,
            20,
            0,
            narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
            .map { messageResponse -> messageResponse.messages.toDomain() }
    }

    fun registerMessageQueue(
        streamName: String,
        topicName: String
    ): Single<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("message"))

        return api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
    }

    fun getQueueMessages(
        currentMessageQueueId: String,
        lastId: Int
    ): Single<GetMessageEventsResponse> {
        return api.getEventsFromQueue(currentMessageQueueId, lastId)
    }

    fun registerReactionQueue(
        streamName: String,
        topicName: String
    ): Single<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("reaction"))

        return api.getQueue(type, narrow)
            .subscribeOn(Schedulers.io())
    }

    fun getQueueReactions(queueId: String, lastId: Int): Single<GetEmojiEventsResponse> {
        return api.getEmojiEventsFromQueue(queueId, lastId)
    }

    fun getMessage(messageId: Int) = api.getMessage(messageId)


    fun onEmojiClick(emojiName: String, messageId: Int): Single<CreateReactionResponse> {
        return api.getMessage(id = messageId, applyMarkdown = true)
            .map { response ->
                response.message.reactions.filter { reaction -> reaction.emojiName == emojiName }
                    .any { it.userId == Reactions.MY_USER_ID }
            }
            .flatMap { isAlreadyClicked ->
                updateEmoji(messageId, emojiName, isAlreadyClicked)
            }
            .subscribeOn(Schedulers.io())
    }

    private fun updateEmoji(
        messageId: Int,
        emojiName: String,
        isAlreadyClicked: Boolean = false
    ): Single<CreateReactionResponse> {
        return when {
            isAlreadyClicked -> api.deleteEmoji(msgId = messageId, emojiName)
            else -> api.addEmoji(msgId = messageId, emojiName)
        }
    }

    fun sendMessage(
        messageText: String,
        streamName: String,
        topicName: String
    ): Single<CreateMessageResponse> {
        return api.sendMessage(
            to = streamName,
            topic = topicName,
            content = messageText
        )
            .subscribeOn(Schedulers.io())
    }

    private fun updateDb(
        messagesInDb: List<MessageEntity>, newMessage: MessageResponse, topicName: String
    ): Completable {
        val msgExist = messagesInDb.any { it.msgId == newMessage.msgId }

        return when {
            msgExist -> {
                Completable.fromCallable {
                    messageDao.updateMessageReactions(
                        newMessage.msgId,
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
            }

            messagesInDb.size < 50 -> {
                Completable.fromCallable {
                    messageDao.insertMessageWithReactions(
                        newMessage.toEntity(topicName),
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
            }

            else -> {
                Completable.fromCallable {
                    messageDao.deleteFirstAndAddNewMessage(
                        messagesInDb.first().msgId,
                        newMessage.toEntity(topicName),
                        newMessage.reactions.toEntity(newMessage.msgId, topicName)
                    )
                }
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