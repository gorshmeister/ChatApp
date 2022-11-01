package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Observable
import io.reactivex.Single
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class StreamOnStreamClickMiddleware : Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.OnStreamClick::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .flatMap { (action, _) ->
                Observable.just(action.streamId)
                    .flatMap { expandableStream(action.items, action.streamId) }
                    .map<StreamAction> { StreamInternalAction.ExpandStream(it) }
                    .onErrorReturn { StreamInternalAction.LoadError(it) }
            }
    }

    private fun expandableStream(
        items: List<ViewTyped>,
        targetStreamId: Int
    ): Observable<List<ViewTyped>> {
        val toDeleteIds = mutableListOf<Int>()
        return Observable.fromCallable {
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

}