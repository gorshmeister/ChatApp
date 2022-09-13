package ru.gorshenev.themesstyles.presentation.ui.chat

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.repositories.ChatDataSource
import ru.gorshenev.themesstyles.data.repositories.ReactionsData
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils

class ChatPresenter(private val view: ChatView) {

    private val compositeDisposable = CompositeDisposable()

    private var displayedItems: List<ViewTyped> = listOf()


    fun loadMessages(count: Int) {
        ChatDataSource.getMessage(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(messages)
                },
                { error -> view.showError(error) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun addReaction(resultPick: BottomSheet.EmojiPickResult) {
        Single.just(resultPick)
            .flatMap { (id, code) ->
                addReactions(displayedItems, id, code)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updList ->
                    displayedItems = updList
                    view.showItems(updList)
                },
                { error ->
                    when (error) {
                        is Errors.ReactionAlreadyExist -> view.showToast()
                        else -> view.showError(error)
                    }
                })
            .apply { compositeDisposable.add(this) }
    }

    fun onEmojiClick(emojiCode: Int, messageId: Int) {
        Single.just(emojiCode to messageId)
            .flatMap { (code, id) ->
                updateEmojisCounter(displayedItems, code, id)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updList ->
                    displayedItems = updList
                    view.showItems(updList)
                },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }
    }

    fun sendMessage(message: String) {
        val lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
        if (lastDate != Utils.getCurrentDate()) {
            displayedItems = displayedItems + DateUi(
                id = displayedItems.size + 1,
                text = Utils.getCurrentDate(),
            )
        }

        try {
            displayedItems = displayedItems + MessageRightUi(
                id = displayedItems.size + 1,
                text = message,
                time = Utils.getCurrentTime(),
                emojis = emptyList()
            )
            if (displayedItems.size % 5 == 0) {
                throw Errors.MessageError("Owi6ka oTnpaBku coo6weHu9I")
            }
        } catch (e: Errors.MessageError) {
            view.showError(e)
        }

        view.showItems(displayedItems)
    }

    fun onClear() {
        compositeDisposable.clear()
    }

    private fun addReactions(
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

    private fun updateEmojisCounter(
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

}
