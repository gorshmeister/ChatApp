package ru.gorshenev.themesstyles.data.mappers

import io.reactivex.Single
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.repositories.ReactionsData
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.channels.items.*
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi

object StreamMapper {


    fun expandableStream(
        items: List<ViewTyped>,
        targetStreamId: Int
    ): Single<List<ViewTyped>> {
        val toDeleteIds = mutableListOf<Int>()
        return Single.fromCallable {
            items.flatMap { item ->
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
    }

    fun updateEmojisCounter(
        messages: List<ViewTyped>,
        emojiCode: Int,
        messageId: Int
    ): Single<List<ViewTyped>> {
        return Single.fromCallable {
            messages.map { item ->
                when (item) {
                    is MessageRightUi -> {
                        val updatedEmojis = item.emojis.map {
                            val isTargetEmoji = it.code == emojiCode && item.id == messageId
                            val isMeClicked = it.user_id.contains(ReactionsData.MY_USER_ID)
                            when {
                                isTargetEmoji && !isMeClicked -> {
                                    it.copy(
                                        isSelected = true,
                                        user_id = it.user_id + listOf(ReactionsData.MY_USER_ID),
                                        counter = it.counter + 1
                                    )
                                }
                                isTargetEmoji && isMeClicked -> {
                                    it.copy(
                                        isSelected = false,
                                        user_id = it.user_id - listOf(ReactionsData.MY_USER_ID),
                                        counter = it.counter - 1
                                    )
                                }
                                else -> it
                            }
                        }
                        item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
                    }
                    is MessageLeftUi -> {
                        val updatedEmojis = item.emojis.map {
                            if (it.code == emojiCode && item.id == messageId && !it.user_id.contains(
                                    ReactionsData.MY_USER_ID
                                )
                            ) {
                                it.copy(
                                    isSelected = true,
                                    user_id = it.user_id + listOf(ReactionsData.MY_USER_ID),
                                    counter = it.counter + 1
                                )
                            } else if (it.code == emojiCode && item.id == messageId && it.user_id.contains(
                                    ReactionsData.MY_USER_ID
                                )
                            ) {
                                it.copy(
                                    isSelected = false,
                                    user_id = it.user_id - listOf(ReactionsData.MY_USER_ID),
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
    }


    fun addReactions(
        items: List<ViewTyped>,
        messageId: Int,
        emojiCode: Int,
    ): Single<List<ViewTyped>> {

        return Single.fromCallable {
            items.map { item ->
                when (item) {
                    is MessageRightUi -> {
                        if (item.id == messageId) {
                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                            if (isEmojiExists) {
                                throw Errors.ReactionAlreadyExist()
                            } else {
                                item.copy(
                                    emojis = item.emojis + EmojiUi(
                                        code = emojiCode,
                                        counter = 1,
                                        isSelected = true,
                                        message_id = messageId,
                                        user_id = listOf(ReactionsData.MY_USER_ID),
                                    )
                                )
                            }
                        } else {
                            item
                        }
                    }
                    is MessageLeftUi -> {
                        if (item.id == messageId) {
                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
                            if (isEmojiExists) {
                                throw Errors.ReactionAlreadyExist()
                            } else {
                                item.copy(
                                    emojis = item.emojis + EmojiUi(
                                        code = emojiCode,
                                        counter = 1,
                                        isSelected = true,
                                        message_id = messageId,
                                        user_id = listOf(ReactionsData.MY_USER_ID),
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

}