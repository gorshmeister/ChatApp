package ru.gorshenev.themesstyles.presentation.ui.channels

import io.reactivex.Observable
import ru.gorshenev.themesstyles.presentation.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class StreamOnTopicClickMiddleware : Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.OnTopicClick::class.java)
            .withLatestFrom(state) { action, currentState -> action to currentState }
            .flatMap { (action, _) ->
                Observable.fromCallable {
                    val topic =
                        action.items.filterIsInstance<TopicUi>().find { it.id == action.topicId }
                    val stream =
                        action.items.filterIsInstance<StreamUi>()
                            .find { it.topics.contains(topic) }

                    if (topic != null && stream != null)
                        return@fromCallable topic to stream
                    else
                        return@fromCallable null
                }
                    .map<StreamAction> {StreamInternalAction.OpenChat(it.first, it.second) }
                    .onErrorReturn { StreamInternalAction.LoadError(it) }
            }
    }
}