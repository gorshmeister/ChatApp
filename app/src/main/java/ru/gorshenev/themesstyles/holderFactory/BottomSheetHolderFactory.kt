package ru.gorshenev.themesstyles.holderFactory

import android.view.View
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holders.TextViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.holders.ReactionsViewHolder

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