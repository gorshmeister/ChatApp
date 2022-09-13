package ru.gorshenev.themesstyles.presentation.ui.chat

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.gorshenev.themesstyles.data.network.Network
import ru.gorshenev.themesstyles.data.network.ZulipApi
import ru.gorshenev.themesstyles.data.network.model.CreateReactionResponse
import ru.gorshenev.themesstyles.data.network.model.Message
import ru.gorshenev.themesstyles.data.network.model.Reaction
import ru.gorshenev.themesstyles.data.repositories.Reactions.MY_USER_ID
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi
import ru.gorshenev.themesstyles.utils.Utils
import ru.gorshenev.themesstyles.utils.Utils.toEmojiCode
import java.util.concurrent.TimeUnit

class ChatPresenter(private val view: ChatView) {

    private val compositeDisposable = CompositeDisposable()

    private var displayedItems: List<ViewTyped> = listOf()

    private val api: ZulipApi = Network.api

    @Serializable
    data class Narrow(
        val operator: String,
        val operand: String
    )

    fun loadMessages(stream: String, topic: String) {
        val narrow = Json.encodeToString(
            listOf(
                Narrow("stream", stream),
                Narrow("topic", topic)
            )
        )

        api.getMessages(0, 500, 500, narrow, false)
            .flatMap { response -> createMessageUi(response.messages) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    displayedItems = messages
                    view.showItems(messages)
                },
                { err -> view.showError(err) },
                { view.stopLoading() }
            ).apply { compositeDisposable.add(this) }
    }

    fun messageQueue(stream: String, topic: String) {
        val narrow = (mapOf("stream" to stream, "topic" to topic))

//        val type = Json.encodeToString(listOf("message", "reaction"))

        Observable.interval(0, 5000, TimeUnit.MILLISECONDS)
            .flatMap { api.getQueue(narrow) }
            .distinct()
            .concatMap { api.getEventsFromQueue(it.queueId) }
            .map { eventResponse -> eventResponse.events.map { it.message } }
            .flatMap { messages -> createMessageUi(messages) }
            .retry()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { items ->
                    displayedItems = displayedItems + items
                    view.showItems(displayedItems)
                    view.scrollMsgsToTheEnd()
                    Log.d("qweqwe", items.toString())
                },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    private fun createMessageUi(
        messages: List<Message>,
    ): Observable<List<ViewTyped>> {
        val list = mutableSetOf<ViewTyped>()
        messages.forEach { message ->
//            val currentDate = Utils.getDateFromUnix(message.time)
//            var lastDate = ""
//
//            if (displayedItems.isNotEmpty()) {
//                lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
//            } else {
//                if (list.isEmpty()) list += DateUi(message.msgId, currentDate)
//                lastDate = (list.findLast { it is DateUi } as DateUi).text
//            }
//
//            if (lastDate != currentDate) list += DateUi(message.msgId, currentDate)


            when (message.senderId) {
                MY_USER_ID -> {
                    list += MessageRightUi(
                        id = message.msgId,
                        text = message.content,
                        time = Utils.getTimeFromUnix(message.time),
                        emojis = createEmojiUi(message.reactions, message.msgId)
                    )
                }
                else -> {
                    list += MessageLeftUi(
                        id = message.msgId,
                        name = message.senderName,
                        text = message.content,
                        emojis = createEmojiUi(message.reactions, message.msgId),
                        time = Utils.getTimeFromUnix(message.time),
                        avatar = message.avatarUrl
                    )
                }
            }
        }
        return Observable.just(list.toList())
    }

    private fun createEmojiUi(reactions: List<Reaction>, messageId: Int): List<EmojiUi> {
        val list = mutableListOf<EmojiUi>()
        reactions.forEach { reaction ->
            val sameEmojiUi = list.find { it.name == reaction.emojiName }

            if (sameEmojiUi != null) {
                val index = list.indexOf(sameEmojiUi)
                list.remove(sameEmojiUi)

                val updItem = sameEmojiUi.copy(
                    listUsersId = sameEmojiUi.listUsersId + listOf(reaction.userId),
                    counter = sameEmojiUi.counter + 1,
                    isSelected = reaction.userId == MY_USER_ID
                )
                list.add(index, updItem)
            } else {
                list += EmojiUi(
                    msgId = messageId,
                    name = reaction.emojiName,
                    code = reaction.emojiCode.toEmojiCode(),
                    listUsersId = listOf(reaction.userId),
                    counter = +1,
                    isSelected = reaction.userId == MY_USER_ID
                )
            }
        }
        return list.toList()
    }

    fun onEmojiClick(emojiName: String, messageId: Int) {
        api.getMessage(id = messageId, apply_markdown = true)
            .map { response -> response.message.reactions.filter { reaction -> reaction.emojiName == emojiName } }
            .map { reactions -> reactions.any { it.userId == MY_USER_ID } }
            .concatMapSingle { isMyClick -> updateEmoji(messageId, emojiName, isMyClick) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.updateMessages()
                },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
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

    fun sendMessage(message: String, stream: String, topic: String) {
        api.sendMessage(
            to = stream,
            topic = topic,
            content = message
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { view.scrollMsgsToTheEnd() },
                { err -> view.showError(err) },
            ).apply { compositeDisposable.add(this) }
    }

    fun onClear() {
        compositeDisposable.clear()
    }

//    private fun addReactions(
//        items: List<ViewTyped>,
//        messageId: Int,
//        emojiCode: Int,
//    ): Single<List<ViewTyped>> {
//        return Single.fromCallable {
//            items.map { item ->
//                when (item) {
//                    is MessageRightUi -> {
//                        if (item.id == messageId) {
//                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
//                            if (isEmojiExists) {
//                                throw Errors.ReactionAlreadyExist()
//                            } else {
//                                item.copy(
//                                    emojis = item.emojis + EmojiUi(
//                                        code = emojiCode,
//                                        counter = 1,
//                                        isSelected = true,
//                                        msgId = messageId,
//                                        listUsersId = listOf(MY_USER_ID),
//                                    )
//                                )
//                            }
//                        } else {
//                            item
//                        }
//                    }
//                    is MessageLeftUi -> {
//                        if (item.id == messageId) {
//                            val isEmojiExists = item.emojis.map { it.code }.contains(emojiCode)
//                            if (isEmojiExists) {
//                                throw Errors.ReactionAlreadyExist()
//                            } else {
//                                item.copy(
//                                    emojis = item.emojis + EmojiUi(
//                                        code = emojiCode,
//                                        counter = 1,
//                                        isSelected = true,
//                                        msgId = messageId,
//                                        listUsersId = listOf(MY_USER_ID),
//                                    )
//                                )
//                            }
//                        } else {
//                            item
//                        }
//                    }
//                    else -> item
//                }
//            }
//        }
//
//    }

//    private fun updateEmojisCounter(
//        messages: List<ViewTyped>,
//        emojiCode: Int,
//        messageId: Int
//    ): Single<List<ViewTyped>> {
//        return Single.fromCallable {
//            messages.map { item ->
//                when (item) {
//                    is MessageRightUi -> {
//                        val updatedEmojis = item.emojis.map {
//                            val isTargetEmoji = it.code == emojiCode && item.id == messageId
//                            val isMeClicked = it.listUsersId.contains(MY_USER_ID)
//                            when {
//                                isTargetEmoji && !isMeClicked -> {
//                                    it.copy(
//                                        isSelected = true,
//                                        listUsersId = it.listUsersId + listOf(MY_USER_ID),
//                                        counter = it.counter + 1
//                                    )
//                                }
//                                isTargetEmoji && isMeClicked -> {
//                                    it.copy(
//                                        isSelected = false,
//                                        listUsersId = it.listUsersId - listOf(MY_USER_ID),
//                                        counter = it.counter - 1
//                                    )
//                                }
//                                else -> it
//                            }
//                        }
//                        item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
//                    }
//                    is MessageLeftUi -> {
//                        val updatedEmojis = item.emojis.map {
//                            if (it.code == emojiCode && item.id == messageId && !it.listUsersId.contains(
//                                    MY_USER_ID
//                                )
//                            ) {
//                                it.copy(
//                                    isSelected = true,
//                                    listUsersId = it.listUsersId + listOf(MY_USER_ID),
//                                    counter = it.counter + 1
//                                )
//                            } else if (it.code == emojiCode && item.id == messageId && it.listUsersId.contains(
//                                    MY_USER_ID
//                                )
//                            ) {
//                                it.copy(
//                                    isSelected = false,
//                                    listUsersId = it.listUsersId - listOf(MY_USER_ID),
//                                    counter = it.counter - 1
//                                )
//                            } else {
//                                it
//                            }
//                        }
//                        item.copy(emojis = updatedEmojis.filter { it.counter != 0 })
//                    }
//                    else -> item
//                }
//            }
//        }
//    }

//    fun loadMessages(count: Int) {
//        ChatDataSource.getMessage(count)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { messages ->
//                    displayedItems = messages
//                    view.showItems(messages)
//                },
//                { error -> view.showError(error) }
//            )
//            .apply { compositeDisposable.add(this) }
//    }

//    fun sendMessage(message: String) {
//        val lastDate = (displayedItems.findLast { it is DateUi } as DateUi).text
//        if (lastDate != Utils.getCurrentDate()) {
//            displayedItems = displayedItems + DateUi(
//                id = displayedItems.size + 1,
//                text = Utils.getCurrentDate(),
//            )
//        }
//
//        try {
//            displayedItems = displayedItems + MessageRightUi(
//                id = displayedItems.size + 1,
//                text = message,
//                time = Utils.getCurrentTime(),
//                emojis = emptyList()
//            )
//            if (displayedItems.size % 5 == 0) {
//                throw Errors.MessageError("Owi6ka oTnpaBku coo6weHu9I")
//            }
//        } catch (e: Errors.MessageError) {
//            view.showError(e)
//        }
//
//        view.showItems(displayedItems)
//    }

//    fun addReaction(resultPick: BottomSheet.EmojiPickResult) {
//        Single.just(resultPick)
//            .flatMap { (id, code) ->
//                addReactions(displayedItems, id, code)
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { updList ->
//                    displayedItems = updList
//                    view.showItems(updList)
//                },
//                { error ->
//                    when (error) {
//                        is Errors.ReactionAlreadyExist -> view.showToast()
//                        else -> view.showError(error)
//                    }
//                })
//            .apply { compositeDisposable.add(this) }
//    }

//    fun onEmojiClick(emojiCode: Int, messageId: Int) {
//        Single.just(emojiCode to messageId)
//            .flatMap { (code, id) ->
//                updateEmojisCounter(displayedItems, code, id)
//            }
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { updList ->
//                    displayedItems = updList
//                    view.showItems(updList)
//                },
//                { error -> view.showError(error) })
//            .apply { compositeDisposable.add(this) }
//    }


}
