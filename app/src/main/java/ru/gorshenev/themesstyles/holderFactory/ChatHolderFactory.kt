package ru.gorshenev.themesstyles.holderFactory

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holders.DateViewHolder
import ru.gorshenev.themesstyles.holders.MessageLeftViewHolder
import ru.gorshenev.themesstyles.holders.MessageRightViewHolder

class ChatHolderFactory(
    private val longClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit
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