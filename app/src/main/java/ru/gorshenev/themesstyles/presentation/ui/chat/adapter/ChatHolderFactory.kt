package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.base_recycler_view.HolderFactory

class ChatHolderFactory(
    private val longClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiName: String, messageId: Int) -> Unit
) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_date -> DateViewHolder(view)
            R.layout.view_message_left -> MessageLeftViewHolder(
                view = view,
                onMessageClick = longClick,
                onEmojiClick = onEmojiClick
            )
            R.layout.view_message_right -> MessageRightViewHolder(
                view = view,
                onMessageClick = longClick,
                onEmojiClick = onEmojiClick
            )
            else -> null
        }
    }
}