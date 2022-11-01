package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseState
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

sealed class StreamState : BaseState {
    object Loading : StreamState()
    object Error : StreamState()
    data class Result(val items: List<ViewTyped>, val visibleItems: List<ViewTyped>) : StreamState()
    data class OpenChat(val topicUi: TopicUi, val streamUi: StreamUi): StreamState()
}