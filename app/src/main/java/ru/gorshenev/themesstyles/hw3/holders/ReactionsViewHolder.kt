package ru.gorshenev.themesstyles.hw3.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.rv.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.Utils.toEmojiString
import ru.gorshenev.themesstyles.hw3.items.ReactionsUi

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