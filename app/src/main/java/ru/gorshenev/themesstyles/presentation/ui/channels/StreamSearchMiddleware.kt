package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Observable
import io.reactivex.Single
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import java.util.concurrent.TimeUnit

class StreamSearchMiddleware : Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.SearchStream::class.java)
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMapSingle { initStreamSearch(it.items, it.query) }
            .map<StreamAction> { StreamInternalAction.SearchResult(it) }
            .onErrorReturn { StreamInternalAction.LoadError(it) }
    }

    private fun initStreamSearch(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Single<List<ViewTyped>> {
        val text = searchText.filter { !it.isDigit() }
        val digits = searchText.filter { it.isDigit() }
        val streamUiList = cachedItems.filterIsInstance<StreamUi>()

        return Single.fromCallable {
            streamUiList.filter { stream ->

                val nameContainsText = stream.name.contains(text, true)
                val nameContainsDigits = stream.name.contains(digits, true)
                val topicContainsTextOrDigit = stream.topics.any {
                    it.name.contains(text, true) && it.name.contains(digits, true)
                }
                val nameContainsSearchText = stream.name.contains(searchText, true)
                val topicContainsSearchText =
                    stream.topics.any { it.name.contains(searchText, true) }

                when (true) {
                    digits.isNotEmpty() -> {
                        nameContainsText && nameContainsDigits || topicContainsTextOrDigit
                    }
                    searchText.isNotEmpty() -> {
                        nameContainsSearchText || topicContainsSearchText
                    }
                    else -> true
                }
            }
        }
    }

}