package ru.gorshenev.themesstyles.data.repositories.chat

import ru.gorshenev.themesstyles.data.database.entities.MessageEntity
import ru.gorshenev.themesstyles.data.database.entities.MessageWithReactionsEntity
import ru.gorshenev.themesstyles.data.database.entities.ReactionEntity
import ru.gorshenev.themesstyles.data.network.model.MessageResponse
import ru.gorshenev.themesstyles.data.network.model.ReactionResponse
import ru.gorshenev.themesstyles.domain.model.chat.EmojiModel
import ru.gorshenev.themesstyles.domain.model.chat.MessageModel
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.utils.Utils.toEmojiCode

object ChatMapper {

    @JvmName("toDomainMessageWithReactions")
    fun List<MessageWithReactionsEntity>.toDomain(): List<MessageModel> {
        return this.map { msgWithReact ->
            val messageEntity: MessageEntity = msgWithReact.message
            val reactionEntities = msgWithReact.reactions

            when (messageEntity.senderId) {
                Reactions.MY_USER_ID -> {
                    MessageModel(
                        id = messageEntity.msgId,
                        text = messageEntity.content,
                        time = Utils.getTimeFromUnix(messageEntity.time),
                        emojis = reactionEntities.toDomain(),
                        myMessage = true
                    )
                }
                else -> {
                    MessageModel(
                        id = messageEntity.msgId,
                        name = messageEntity.senderName,
                        text = messageEntity.content,
                        emojis = reactionEntities.toDomain(),
                        time = Utils.getTimeFromUnix(messageEntity.time),
                        avatar = messageEntity.avatarUrl
                    )
                }
            }
        }
    }

    private fun List<ReactionEntity>.toDomain(): List<EmojiModel> {
        val list = mutableListOf<EmojiModel>()
        this.forEach { reaction ->
            val sameEmojiModel = list.find { it.name == reaction.emojiName }

            if (sameEmojiModel != null) {
                val index = list.indexOf(sameEmojiModel)
                list.remove(sameEmojiModel)

                val updItem = sameEmojiModel.copy(
                    listUsersId = sameEmojiModel.listUsersId + listOf(reaction.userId),
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
                list.add(index, updItem)
            } else {
                list += EmojiModel(
                    msgId = reaction.messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
            }
        }
        return list.toList()
    }


    @JvmName("toUiMessageModel")
    fun List<MessageModel>.toUi(): List<ViewTyped> {
        return this.map { messageModel ->

            if (messageModel.myMessage) {
                MessageRightUi(
                    id = messageModel.id,
                    text = messageModel.text,
                    time = messageModel.time,
                    emojis = messageModel.emojis.toUi()
                )
            }
            else {
                MessageLeftUi(
                    id = messageModel.id,
                    name = messageModel.name,
                    text = messageModel.text,
                    emojis = messageModel.emojis.toUi(),
                    time = messageModel.time,
                    avatar = messageModel.avatar
                )
            }
        }
    }

    private fun List<EmojiModel>.toUi(): List<EmojiUi> {
        return this.map { emojiModel ->
            EmojiUi(
                msgId = emojiModel.msgId,
                name = emojiModel.name,
                code = emojiModel.code,
                listUsersId = emojiModel.listUsersId,
                counter = emojiModel.listUsersId.size,
                isSelected = emojiModel.isSelected
            )
        }
    }


    @JvmName("toDomainMessageResponse")
    fun List<MessageResponse>.toDomain(): List<MessageModel> {
        return this.map { messageResponse ->
            when (messageResponse.senderId) {
                Reactions.MY_USER_ID -> {
                    MessageModel(
                        id = messageResponse.msgId,
                        text = messageResponse.content,
                        time = Utils.getTimeFromUnix(messageResponse.time),
                        emojis = messageResponse.reactions.toDomain(messageResponse.msgId),
                        myMessage = true
                    )
                }
                else -> {
                    MessageModel(
                        id = messageResponse.msgId,
                        name = messageResponse.senderName,
                        text = messageResponse.content,
                        emojis = messageResponse.reactions.toDomain(messageResponse.msgId),
                        time = Utils.getTimeFromUnix(messageResponse.time),
                        avatar = messageResponse.avatarUrl
                    )
                }
            }
        }
    }

    private fun List<ReactionResponse>.toDomain(messageId: Int): List<EmojiModel> {
        val list = mutableListOf<EmojiModel>()
        this.forEach { reaction ->
            val sameEmojiModel = list.find { it.name == reaction.emojiName }

            if (sameEmojiModel != null) {
                val index = list.indexOf(sameEmojiModel)
                list.remove(sameEmojiModel)

                val updItem = sameEmojiModel.copy(
                    listUsersId = sameEmojiModel.listUsersId + listOf(reaction.userId),
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
                list.add(index, updItem)
            } else {
                list += EmojiModel(
                    msgId = messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    isSelected = reaction.userId == Reactions.MY_USER_ID
                )
            }
        }
        return list.toList()
    }


    fun MessageResponse.toEntity(topicName: String): MessageEntity {
        return MessageEntity(
            msgId = this.msgId,
            topicName = topicName,
            senderName = this.senderName,
            content = this.content,
            senderId = this.senderId,
            time = this.time,
            avatarUrl = this.avatarUrl,
            subject = this.subject
        )
    }

    fun List<ReactionResponse>.toEntity(messageId: Int, topicName: String): List<ReactionEntity> {
        return this.map { reactionResponse ->
            ReactionEntity(
                messageId = messageId,
                emojiName = reactionResponse.emojiName,
                emojiCode = reactionResponse.emojiCode,
                reactionType = reactionResponse.reactionType,
                userId = reactionResponse.userId,
                topicName = topicName
            )
        }
    }

}