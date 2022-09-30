package ru.gorshenev.themesstyles.data.repositories.chat

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.database.dao.MessageDao
import ru.gorshenev.themesstyles.data.database.dao.MessageWithReactions
import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity
import ru.gorshenev.themesstyles.data.network.model.Message
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.utils.Utils.toEmojiCode

class ChatRepository(private val messageDao: MessageDao) {

    fun getMessagesFromDb(topic: String): Single<List<ViewTyped>> {
        return messageDao.getMessages(topic)
            .map { list -> createMessageUiFromEntity(list) }
            .subscribeOn(Schedulers.io())
            .doAfterSuccess {
                Log.d("database", "==== Messages loaded from DATABASE ==== ")
            }
    }

    private fun updateMessageReactions(message: Message, topicName: String): Completable {
       return messageDao.deleteMessageReactions(message.msgId).andThen(
            messageDao.insert(message.reactions.map { reaction ->
                ReactionEntity(
                    messageId = message.msgId,
                    emojiName = reaction.emojiName,
                    emojiCode = reaction.emojiCode,
                    reactionType = reaction.reactionType,
                    userId = reaction.userId,
                    topicName = topicName
                )
            })
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({}, { Log.d("database", "UPDATE MESSAGE ERROR $it") })
    }

    private fun deleteFirstMessage(messages: List<MessageEntity>): Completable {
            return messageDao.deleteMessage(messages.first().msgId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun insert(message: Message, topicName: String): Completable {
        return messageDao.insert(
                MessageEntity(
                    topicName = topicName,
                    msgId = message.msgId,
                    senderName = message.senderName,
                    content = message.content,
                    senderId = message.senderId,
                    time = message.time,
                    avatarUrl = message.avatarUrl,
                    subject = message.subject
                )
            ).andThen(
            messageDao.insert(message.reactions.map { reaction ->
                ReactionEntity(
                    messageId = message.msgId,
                    emojiName = reaction.emojiName,
                    emojiCode = reaction.emojiCode,
                    reactionType = reaction.reactionType,
                    userId = reaction.userId,
                    topicName = topicName
                )
            }
            ))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun updDB(
        messages: List<MessageEntity>,
        message: Message,
        topicName: String
    ): Completable {
        val msgDoesNotExist = messages.all { it.msgId != message.msgId }
        val msgExist = messages.any { it.msgId == message.msgId }

        return when {
            messages.isEmpty() || msgDoesNotExist && messages.size != 50 -> {
                insert(message, topicName)
            }

            msgExist -> {
                updateMessageReactions(message, topicName)
            }

            msgDoesNotExist && messages.size == 50 -> {
                deleteFirstMessage(messages)
                insert(message, topicName)
            }

            else -> {
                Log.d("database", "add to data ERROR")
                Completable.error(Error("add to data ERROR"))
            }
        }
    }


    fun addToDatabase(
        message: Message,
        topicName: String
    ): Completable {
       return messageDao.getMessagesFromTopic(topicName)
            .flatMapCompletable { messageEntityList -> updDB(messageEntityList, message, topicName) }
            .subscribeOn(Schedulers.io())
    }


    private fun createMessageUiFromEntity(
        list: List<MessageWithReactions>
    ): List<ViewTyped> {
        return list.map { msgWithReact ->
            val messageEntity: MessageEntity = msgWithReact.message!!
            val reactionEntities = msgWithReact.reactions

            when (messageEntity.senderId) {
                Reactions.MY_USER_ID -> {
                    MessageRightUi(
                        id = messageEntity.msgId,
                        text = messageEntity.content,
                        time = Utils.getTimeFromUnix(messageEntity.time),
                        emojis = createEmojiUi(reactionEntities)
                    )
                }
                else -> {
                    MessageLeftUi(
                        id = messageEntity.msgId,
                        name = messageEntity.senderName,
                        text = messageEntity.content,
                        emojis = createEmojiUi(reactionEntities),
                        time = Utils.getTimeFromUnix(messageEntity.time),
                        avatar = messageEntity.avatarUrl
                    )
                }
            }
        }
    }

    private fun createEmojiUi(reactions: List<ReactionEntity>): List<EmojiUi> {
        val list = mutableListOf<EmojiUi>()
        reactions.forEach { reaction ->
            val sameEmojiUi = list.find { it.name == reaction.emojiName }

            if (sameEmojiUi != null) {
                val index = list.indexOf(sameEmojiUi)
                list.remove(sameEmojiUi)

                val updItem = sameEmojiUi.copy(
                    listUsersId = sameEmojiUi.listUsersId + listOf(reaction.userId),
                    counter = sameEmojiUi.counter + 1,
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
                list.add(index, updItem)
            } else {
                list += EmojiUi(
                    msgId = reaction.messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    counter = +1,
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
            }
        }
        return list.toList()
    }

}