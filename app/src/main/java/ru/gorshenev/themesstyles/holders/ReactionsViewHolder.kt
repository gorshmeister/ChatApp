package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.databinding.ItemEmojiBinding
import ru.gorshenev.themesstyles.items.ReactionsUi

class ReactionsViewHolder(
    view: View,
    private val onEmojiClick: (emojiCode: Int) -> Unit
) : BaseViewHolder<ReactionsUi>(view) {
    private val binding: ItemEmojiBinding by viewBinding()

    private var currentCode: Int? = null

    init {
        binding.emojiHolder.setOnClickListener {
            currentCode?.let(onEmojiClick)
        }
    }

    override fun bind(item: ReactionsUi) {
        currentCode = item.emojiCode
        binding.emojiHolder.text = item.emojiCode.toEmojiString()
    }
}