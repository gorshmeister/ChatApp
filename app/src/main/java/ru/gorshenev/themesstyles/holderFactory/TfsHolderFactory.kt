package ru.gorshenev.themesstyles.holderFactory

import android.view.View
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holders.TextViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.holders.LeftMessageViewHolder
import ru.gorshenev.themesstyles.holders.RightMessageViewHolder

class TfsHolderFactory(
	private val longClick: ((messageId: Int) -> Unit),
	private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit
) : HolderFactory() {

	override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
		return when (viewType) {
			R.layout.item_text, R.layout.item_date -> TextViewHolder(view)
			R.layout.view_message_left -> LeftMessageViewHolder(
				view = view,
				onMessageClick = longClick,
				onEmojiClick = onEmojiClick
			)
			R.layout.view_message_right -> RightMessageViewHolder(
				view = view,
				onMessageClick = longClick,
				onEmojiClick = onEmojiClick
			)
			else -> null
		}
	}
}