package ru.gorshenev.themesstyles.presentation.ui.chat

import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseAction
import ru.gorshenev.themesstyles.presentation.mvi_core.BaseEffect

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
        val isBottomSheetClick: Boolean = false
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
    object EmptyAction : ChatInternalAction()
    object ScrollToTheEnd : ChatInternalAction()
    object StartLoading : ChatInternalAction()
    data class LoadError(val error: Throwable) : ChatInternalAction()
    data class LoadResult(val items: List<ViewTyped>) : ChatInternalAction()
    data class ReactionExist(val error: Throwable) : ChatInternalAction()
}

sealed class ChatEffect : BaseEffect {
    data class SnackBar(val error: Throwable) : ChatEffect()
    data class Toast(val error: Throwable) : ChatEffect()
    object Scroll: ChatEffect()
}