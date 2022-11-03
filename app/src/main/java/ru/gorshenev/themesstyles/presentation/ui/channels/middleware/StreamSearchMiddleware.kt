package ru.gorshenev.themesstyles.presentation.ui.channels.middleware

import io.reactivex.Observable
import io.reactivex.Single
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamInternalAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamState
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
        return Single.fromCallable {
            cachedItems.filterIsInstance<StreamUi>().filter { stream ->
                val nameContainsSearchText = stream.name.contains(searchText, true)
                val topicContainsSearchText =
                    stream.topics.any { it.name.contains(searchText, true) }

                nameContainsSearchText || topicContainsSearchText
            }
        }
    }
}