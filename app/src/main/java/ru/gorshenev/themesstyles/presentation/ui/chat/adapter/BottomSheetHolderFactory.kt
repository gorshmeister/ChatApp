package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory
import ru.gorshenev.themesstyles.R

class BottomSheetHolderFactory(
	private val onEmojiClick: (emojiName: String, emojiCode: String) -> Unit
) : HolderFactory() {

	override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
		return when (viewType) {
			R.layout.item_emoji -> ReactionsViewHolder(
				view = view,
				onEmojiClick = onEmojiClick
			)
			else -> null
		}
	}
}