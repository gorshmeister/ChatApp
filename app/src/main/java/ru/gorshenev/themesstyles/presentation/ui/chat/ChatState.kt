package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseState

//data class ChatState(
//    val isLoading: Boolean = false,
//    val isError: Boolean = false,
//    val result: List<ViewTyped> = emptyList()
//) : BaseState

sealed class ChatState : BaseState {
    object Loading : ChatState()
    object Error : ChatState()
    data class Result(val items: List<ViewTyped>) : ChatState()
}