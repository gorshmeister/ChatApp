package ru.gorshenev.themesstyles.hw3.holderFactory

import android.view.View
import ru.gorshenev.rv.BaseViewHolder
import ru.gorshenev.rv.HolderFactory
import ru.gorshenev.themesstyles.hw3.holders.TextViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.holders.ReactionsViewHolder

class BottomSheetHolderFactory(
	private val onEmojiClick: (emojiCode: Int) -> Unit
) : HolderFactory() {

	override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
		return when (viewType) {
			R.layout.item_text -> TextViewHolder(view)
			R.layout.item_emoji -> ReactionsViewHolder(
				view = view,
				onEmojiClick = onEmojiClick
			)
			else -> null
		}
	}
}