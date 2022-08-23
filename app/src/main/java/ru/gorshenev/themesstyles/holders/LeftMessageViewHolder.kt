package ru.gorshenev.themesstyles.holders

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.EmojiView
import ru.gorshenev.themesstyles.Utils.px
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupLeftBinding
import ru.gorshenev.themesstyles.items.EmojiUi
import ru.gorshenev.themesstyles.items.LeftMessageUi


class LeftMessageViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit
) : BaseViewHolder<LeftMessageUi>(view) {
    private val binding: ViewCustomViewGroupLeftBinding by viewBinding()

    override fun bind(item: LeftMessageUi) {
        itemView.setOnLongClickListener {
            onMessageClick(item.id)
            true
        }

        with(binding) {
            ivMsgAvatar.setImageResource(item.avatar)
            tvMsgName.text = item.name
            tvMsgText.text = item.text
            tvMsgTime.text = item.time

            flexbox.removeAllViews()
            flexbox.addViews(
                item.emojis.map { emojiUi: EmojiUi ->
                    EmojiView(flexbox.context).apply {
                        text = emojiUi.code.toEmojiString()
                        count = emojiUi.counter
                        messageId = emojiUi.message_id
                        userId += emojiUi.user_id
                        isSelected = emojiUi.isSelected
                        setOnClickListener { onEmojiClick(emojiUi.code, item.id) }
                    }
                }
            )
            if (item.emojis.isNotEmpty()) {
                flexbox.addViews(listOf(EmojiView(flexbox.context).apply {
                    text = "+"
                    this.setSize(48.px, 29.px)
                    setOnClickListener { onMessageClick(item.id) }
                }))
            }
        }
    }
}


