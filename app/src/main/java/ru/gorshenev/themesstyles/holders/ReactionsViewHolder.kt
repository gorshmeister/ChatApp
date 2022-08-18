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

    private var currentCode: Int? = null

    val emoji: TextView = view.findViewById(R.id.emojiHolder)

    init {
        emoji.setOnClickListener {
            currentCode?.let(onEmojiClick)
        }
    }

    override fun bind(item: ReactionsUi) {
        currentCode = item.emojiCode
        emoji.text = item.emojiCode.toEmojiString()
    }
}