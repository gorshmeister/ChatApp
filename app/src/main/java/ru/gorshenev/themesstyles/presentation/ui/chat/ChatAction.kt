package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseAction
import ru.gorshenev.themesstyles.presentation.base.mvi_core.BaseEffect
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

sealed class ChatAction : BaseAction {
    data class UploadMessages(
        val streamName: String,
        val topicName: String
    ) : ChatAction()

    data class UploadMoreMessages(
        val streamName: String,
        val topicName: String,
    ) : ChatAction()

    data class SendMessage(
        val messageText: String,
        val streamName: String,
        val topicName: String
    ) : ChatAction()

    data class OnEmojiClick(
        val emojiName: String,
        val messageId: Int,
        val isBottomSheetClick: Boolean
    ) : ChatAction()

    data class RegisterMessageQueue(
        val streamName: String,
        val topicName: String,
    ) : ChatAction()

    data class GetQueueMessage(
        val queueId: String,
        val lastId: Int,
        val topicName: String,
        val items: List<ViewTyped>
    ) : ChatAction()

    data class RegisterReactionQueue(
        val streamName: String,
        val topicName: String,
    ) : ChatAction()

    data class GetQueueReaction(
        val queueId: String,
        val lastId: Int,
        val topicName: String,
        val items: List<ViewTyped>
    ) : ChatAction()

}

sealed class ChatInternalAction : ChatAction() {
    object StartLoading : ChatInternalAction()
    object ScrollToTheEnd : ChatInternalAction()
    object StartPaginationLoading : ChatInternalAction()
    data class LoadError(val error: Throwable) : ChatInternalAction()
    data class LoadResult(val items: List<ViewTyped>) : ChatInternalAction()
}

sealed class ChatEffect : BaseEffect {
    object Scroll : ChatEffect()
    object ProgressBar : ChatEffect()
    data class SnackBar(val error: Throwable) : ChatEffect()
}