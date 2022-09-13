package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemEmojiBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.items.ReactionsUi
import ru.gorshenev.themesstyles.utils.Utils.toEmojiString

class ReactionsViewHolder(
    view: View,
    private val onEmojiClick: (emojiName: String) -> Unit
) : BaseViewHolder<ReactionsUi>(view) {
    private val binding: ItemEmojiBinding by viewBinding()

    private var currentName: String? = null

    init {
        binding.emojiHolder.setOnClickListener {
            currentName?.let(onEmojiClick)
        }
    }

    override fun bind(item: ReactionsUi) {
        currentName = item.name
        binding.emojiHolder.text = item.code.toEmojiString()
    }
}