package ru.gorshenev.themesstyles.presentation.ui.channels

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseState

sealed class StreamState : BaseState {
    object Loading : StreamState()
    object Error : StreamState()
    data class Result(val items: List<ViewTyped>, val visibleItems: List<ViewTyped>) : StreamState()
}