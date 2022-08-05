package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.items.ReactionsUi

class ReactionsViewHolder(
    view: View,
    private val onEmojiClick: (emojiCode: Int) -> Unit
) : BaseViewHolder<ReactionsUi>(view) {

    val emoji: TextView = view.findViewById(R.id.emojiHolder)

    override fun bind(item: ReactionsUi) {
        emoji.text = item.emojiCode.toEmojiString()

        emoji.setOnClickListener { onEmojiClick(item.emojiCode) }
    }
}