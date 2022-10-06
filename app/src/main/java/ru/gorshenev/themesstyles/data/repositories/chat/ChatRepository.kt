package ru.gorshenev.themesstyles.data.repositories.chat

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.gorshenev.themesstyles.data.database.dao.MessageDao
import ru.gorshenev.themesstyles.data.database.entities.MessageWithReactions
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.*
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toDomain
import ru.gorshenev.themesstyles.data.repositories.chat.ChatMapper.toEntity
import ru.gorshenev.themesstyles.domain.model.chat.MessageModel

class ChatRepository(
    private val messageDao: MessageDao,
    private val api: ZulipApi,
    private val executionScheduler: Scheduler = Schedulers.io(),
) {

    fun getMessage(messageId: Int) = api.getMessage(messageId).subscribeOn(executionScheduler)

    fun getMessages(
        streamName: String,
        topicName: String,
        anchorMessageId: Long,
        numBefore: Int,
        onlyRemote: Boolean,
    ): Observable<List<MessageModel>> {
        val remoteMessages = getMessagesRemote(streamName, topicName, anchorMessageId, numBefore)
        return if (onlyRemote) {
            remoteMessages.map { it.messages.toDomain() }.toObservable()
        } else {
            Single.concatArrayEager(
                getMessagesLocal(topicName).map { it.toDomain() },
                remoteMessages.doOnSuccess { replaceLocalMessages(topicName, it.messages) }.map { it.messages.toDomain() }
            ).subscribeOn(executionScheduler).toObservable()
        }
    }

    fun sendMessage(message: String, streamName: String, topicName: String): Single<CreateMessageResponse> {
        return api.sendMessage(
            to = streamName,
            topic = topicName,
            content = message
        ).subscribeOn(executionScheduler)
    }

    fun saveMessage(topicName: String, newMessage: MessageResponse): Completable {
        return messageDao.getMessages(topicName).flatMapCompletable { localMessages ->
            val isMessageExists = localMessages.any { it.msgId == newMessage.msgId }
            Completable.fromCallable {
                when {
                    isMessageExists -> {
                        messageDao.updateMessageReactions(
                            newMessage.msgId,
                            newMessage.reactions.toEntity(newMessage.msgId, topicName)
                        )
                    }
                    localMessages.size < 50 -> {
                        messageDao.insertMessageWithReactions(
                            newMessage.toEntity(topicName),
                            newMessage.reactions.toEntity(newMessage.msgId, topicName)
                        )
                    }
                    else -> {
                        messageDao.deleteFirstAndAddNewMessage(
                            localMessages.first().msgId,
                            newMessage.toEntity(topicName),
                            newMessage.reactions.toEntity(newMessage.msgId, topicName)
                        )
                    }
                }
            }.subscribeOn(executionScheduler)
        }
    }

    private fun getMessagesRemote(
        streamName: String,
        topicName: String,
        anchorMessageId: Long,
        numBefore: Int,
    ): Single<GetMessageResponse> {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", streamName),
                Narrow("topic", topicName)
            )
        )
        return api.getMessages(
            anchor = anchorMessageId,
            numBefore = numBefore,
            narrow = narrow,
            clientGravatar = false,
            applyMarkdown = false
        )
    }

    private fun getMessagesLocal(topicName: String): Single<List<MessageWithReactions>> {
        return messageDao.getMessagesWithReactions(topicName)
    }

    private fun replaceLocalMessages(topicName: String, remoteMessages: List<MessageResponse>) {
        val messageEntities = remoteMessages.map { it.toEntity(topicName) }
        val reactionEntities = remoteMessages.flatMap { it.reactions.toEntity(it.msgId, topicName) }
        /* can be placed in the dao as a transaction,
        but the signature of transaction method does not guarantee
        that the entities actually have that topic name
         */
        messageDao.deleteMessages(topicName)
        messageDao.insertMessages(messageEntities)
        messageDao.insertReactions(reactionEntities)
    }

    fun registerMessageQueue(streamName: String, topicName: String): Single<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("message"))
        return api.getQueue(type, narrow).subscribeOn(executionScheduler)
    }

    fun getQueueMessages(
        currentMessageQueueId: String,
        lastId: Int,
    ): Single<GetMessageEventsResponse> {
        return api.getEventsFromQueue(currentMessageQueueId, lastId).subscribeOn(executionScheduler)
    }

    fun updateEmoji(emojiName: String, messageId: Int): Single<CreateReactionResponse> {
        return api.getMessage(id = messageId, applyMarkdown = true)
            .map { response ->
                response.message.reactions.filter { reaction -> reaction.emojiName == emojiName }
                    .any { it.userId == Reactions.MY_USER_ID }
            }
            .flatMap { isAlreadyClicked -> updateEmoji(messageId, emojiName, isAlreadyClicked) }
            .subscribeOn(executionScheduler)
    }

    fun registerReactionQueue(streamName: String, topicName: String): Single<CreateQueueResponse> {
        val narrow = (mapOf("stream" to streamName, "topic" to topicName))
        val type = Json.encodeToString(listOf("reaction"))
        return api.getQueue(type, narrow).subscribeOn(executionScheduler)
    }

    fun getQueueReactions(queueId: String, lastId: Int): Single<GetEmojiEventsResponse> {
        return api.getEmojiEventsFromQueue(queueId, lastId).subscribeOn(executionScheduler)
    }

    private fun updateEmoji(
        msgId: Int,
        emojiName: String,
        isAlreadyClicked: Boolean = false,
    ): Single<CreateReactionResponse> {
        return when {
            isAlreadyClicked -> api.deleteEmoji(msgId = msgId, emojiName)
            else -> api.addEmoji(msgId = msgId, emojiName)
        }
    }

    companion object {
        const val DEFAULT_MESSAGE_ANCHOR: Long = 10000000000000000
        const val DEFAULT_NUM_BEFORE: Int = 50
        const val MORE_NUM_BEFORE: Int = 20
    }

}