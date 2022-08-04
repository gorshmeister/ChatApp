package ru.gorshenev.themesstyles.hw3.holderFactory

import android.view.View
import ru.gorshenev.rv.BaseViewHolder
import ru.gorshenev.rv.HolderFactory
import ru.gorshenev.themesstyles.hw3.holders.TextViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.holders.LeftMessageViewHolder
import ru.gorshenev.themesstyles.hw3.holders.RightMessageViewHolder

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