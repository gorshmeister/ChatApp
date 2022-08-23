package ru.gorshenev.themesstyles

import android.content.Context
import android.widget.Toast
import ru.gorshenev.themesstyles.items.*

object StreamMapper {


    fun expandableStream(items: List<ViewTyped>, targetStreamId: Int): List<ViewTyped> {
        val toDeleteIds = mutableListOf<Int>()
        return items.flatMap { item ->
            when (item) {
                is StreamUi -> when {
                    item.id == targetStreamId && !item.isExpanded -> {
                        listOf(item.copy(isExpanded = true)) + item.topics
                    }
                    item.id == targetStreamId && item.isExpanded -> {
                        toDeleteIds.addAll(item.topics.map { it.id })
                        listOf(item.copy(isExpanded = false))
                    }
                    else -> listOf(item)
                }
                is TopicUi -> when (item.id) {
                    in toDeleteIds -> {
                        toDeleteIds - item.id
                        listOf()
                    }
                    else -> listOf(item)
                }

                else -> listOf(item)
            }
        }
    }

    fun updateEmojisCounter(
        messages: List<ViewTyped>,
        emojiCode: Int,
        messageId: Int
    ): List<ViewTyped> {
        return messages.map { item ->
            when (item) {
                is RightMessageUi -> {
                    val updatedEmojis = item.emojis.map {
                        val isTargetEmoji = it.code == emojiCode && item.id == messageId
                        val isMeClicked = it.user_id.contains(Data.MY_USER_ID)
                        when {
                            isTargetEmoji && !isMeClicked -> {
                                it.copy(
                                    isSelected = true,
                                    user_id = it.user_id + listOf(Data.MY_USER_ID),
                                    counter = it.counter + 1
                                )
                            }
                            isTargetEmoji && isMeClicked -> {
                                it.copy(
                                    isSelected = false,
                                    user_id = it.user_id - listOf(Data.MY_USER_ID),
                                    counter = it.counter - 1
                                )
                            }
                            else -> it
                        }
                    }
                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                is LeftMessageUi -> {
                    val updatedEmojis = item.emojis.map {
                        if (it.code == emojiCode && item.id == messageId && !it.user_id.contains(
                                Data.MY_USER_ID
                            )
                        ) {
                            it.copy(
                                isSelected = true,
                                user_id = it.user_id + listOf(Data.MY_USER_ID),
                                counter = it.counter + 1
                            )
                        } else if (it.code == emojiCode && item.id == messageId && it.user_id.contains(
                                Data.MY_USER_ID
                            )
                        ) {
                            it.copy(
                                isSelected = false,
                                user_id = it.user_id - listOf(Data.MY_USER_ID),
                                counter = it.counter - 1
                            )
                        } else {
                            it
                        }
                    }
                    item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                }
                else -> item
            }
        }
    }

    class ReactionAlreadyExist : Exception()

    fun addReactions(
        items: List<ViewTyped>,
        messageId: Int,
        emojiCode: Int,
    ): List<ViewTyped> {

        return items.map { item ->
            when (item) {
                is RightMessageUi -> {
                    if (item.id == messageId) {
                        val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                        if (isEmojiExists) {
                            throw ReactionAlreadyExist()
                        } else {
                            item.copy(
                                emojis = item.emojis + EmojiUi(
                                    code = emojiCode,
                                    counter = 1,
                                    isSelected = true,
                                    message_id = messageId,
                                    user_id = listOf(Data.MY_USER_ID),
                                )
                            )
                        }
                    } else {
                        item
                    }
                }
                is LeftMessageUi -> {
                    if (item.id == messageId) {
                        val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                        if (isEmojiExists) {
                            throw ReactionAlreadyExist()
                        } else {
                            item.copy(
                                emojis = item.emojis + EmojiUi(
                                    code = emojiCode,
                                    counter = 1,
                                    isSelected = true,
                                    message_id = messageId,
                                    user_id = listOf(Data.MY_USER_ID),
                                )
                            )
                        }
                    } else {
                        item
                    }
                }
                else -> item
            }
        }

    }

}