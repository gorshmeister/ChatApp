package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.data.Utils.toEmojiString
import ru.gorshenev.themesstyles.databinding.ItemEmojiBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.items.ReactionsUi

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