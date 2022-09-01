package ru.gorshenev.themesstyles.presentation.ui.chat

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.gorshenev.themesstyles.data.Errors
import ru.gorshenev.themesstyles.data.Utils
import ru.gorshenev.themesstyles.data.mappers.StreamMapper
import ru.gorshenev.themesstyles.data.repositories.ChatDataSource
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.chat.items.DateUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi

class ChatPresenter(private val view: ChatView) {

    private val compositeDisposable = CompositeDisposable()

    private var cachedItems: List<ViewTyped> = listOf()


    fun loadMessages(count: Int) {
        ChatDataSource.getMessage(count)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages ->
                    cachedItems = messages
                    view.showItems(messages) },
                { error -> view.showError(error) }
            )
            .apply { compositeDisposable.add(this) }
    }

    fun addReaction(resultPick: BottomSheet.EmojiPickResult) {
        Observable.just(resultPick)
            .flatMapSingle { (id, code) ->
                StreamMapper.addReactions(cachedItems, id, code)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updList ->
                    cachedItems = updList
                    view.showItems(updList) },
                { error ->
                    when (error) {
                        is Errors.ReactionAlreadyExist -> view.showToast()
                        else -> view.showError(error)
                    }
                })
            .apply { compositeDisposable.add(this) }

    }

    fun onEmojiClick(emojiCode: Int, messageId: Int) {
        Observable.just(emojiCode to messageId)
            .flatMapSingle { (code, id) ->
                StreamMapper.updateEmojisCounter(cachedItems, code, id)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updList ->
                    cachedItems = updList
                    view.showItems(updList) },
                { error -> view.showError(error) })
            .apply { compositeDisposable.add(this) }

    }

    fun sendMessage(message: String) {
        val lastDate = (cachedItems.findLast { it is DateUi } as DateUi).text
        if (lastDate != Utils.getCurrentDate()) {
            cachedItems = cachedItems + DateUi(
                id = cachedItems.size + 1,
                text = Utils.getCurrentDate(),
            )
        }

        try {
            cachedItems = cachedItems + MessageRightUi(
                id = cachedItems.size + 1,
                text = message,
                time = Utils.getCurrentTime(),
                emojis = emptyList()
            )
            if (cachedItems.size % 5 == 0) {
                throw Errors.MessageError("Owi6ka oTnpaBku coo6weHu9I")
            }
        } catch (e: Errors.MessageError) {
            view.showError(e)
        }

        view.showItems(cachedItems)
    }

    fun onClear() {
        compositeDisposable.clear()
    }
}
