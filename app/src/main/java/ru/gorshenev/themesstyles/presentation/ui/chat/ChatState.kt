package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseState

sealed class ChatState : BaseState {
    object Loading : ChatState()
    object Error : ChatState()
    data class Result(val items: List<ViewTyped>, val isPaginationLoading: Boolean) : ChatState()
}