package ru.gorshenev.themesstyles.presentation.ui.channels.middleware

import io.reactivex.Observable
import ru.gorshenev.themesstyles.presentation.base.mvi_core.Middleware
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamInternalAction
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamState
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class OpenChatMiddleware : Middleware<StreamAction, StreamState> {
    override fun bind(
        actions: Observable<StreamAction>,
        state: Observable<StreamState>
    ): Observable<StreamAction> {
        return actions.ofType(StreamAction.OnTopicClick::class.java)
            .flatMap<StreamAction> { action ->
                val topic =
                    action.items.filterIsInstance<TopicUi>().find { it.id == action.topicId }
                val streamName = action.items.filterIsInstance<StreamUi>()
                    .find { it.id == topic?.streamId }?.name

                if (topic?.name != null && streamName != null) {
                    Observable.just(StreamInternalAction.OpenChat(topic.name, streamName))
                } else {
                    Observable.empty()
                }
            }
            .onErrorReturn { StreamInternalAction.LoadError(it) }
    }
}
