package ru.gorshenev.themesstyles.presentation.ui.channels.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamInternalAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamState
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class ExpandStreamMiddleware : Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.OnStreamClick::class.java)
            .map<StreamAction> { action ->
                val updatedItems = expandableStream(action.items, action.streamId)
                StreamInternalAction.StreamExpandedResult(updatedItems)
            }
            .onErrorReturn { StreamInternalAction.LoadError(it) }
    }


    private fun expandableStream(
        items: List<ViewTyped>,
        targetStreamId: Int
    ): List<ViewTyped> {
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
}