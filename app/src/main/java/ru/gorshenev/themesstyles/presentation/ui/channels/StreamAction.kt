package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

sealed class StreamAction : BaseAction {
    class UploadStreams(val streamType: StreamFragment.StreamType) : StreamAction()
    data class SearchStream(val items: List<ViewTyped>, val query: String) : StreamAction()
    data class OnStreamClick(val streamId: Int, val items: List<ViewTyped>) : StreamAction()
    data class OnTopicClick(val topicId: Int, val items: List<ViewTyped>): StreamAction()
}

sealed class StreamInternalAction() : StreamAction() {
    object StartLoading : StreamInternalAction()
    data class LoadError(val error: Throwable) : StreamInternalAction()
    data class LoadResult(val items: List<ViewTyped>) : StreamInternalAction()
    data class SearchResult(val items: List<ViewTyped>) : StreamInternalAction()
    data class ExpandStream(val items: List<ViewTyped>): StreamInternalAction()
    data class OpenChat(val topic: TopicUi, val stream: StreamUi):StreamInternalAction()
}
