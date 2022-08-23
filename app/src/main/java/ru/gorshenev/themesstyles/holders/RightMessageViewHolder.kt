package ru.gorshenev.themesstyles.holders

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.EmojiView
import ru.gorshenev.themesstyles.Utils.px
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupRightBinding
import ru.gorshenev.themesstyles.items.RightMessageUi

class RightMessageViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit,
) :
    BaseViewHolder<RightMessageUi>(view) {
    private val binding: ViewCustomViewGroupRightBinding by viewBinding()

    override fun bind(item: RightMessageUi) {
        itemView.setOnLongClickListener {
            onMessageClick(item.id)
            true
        }
        with(binding) {
            tvMsgText.text = item.text
            tvMsgTime.text = item.time

            flexbox.removeAllViews()
            flexbox.addViews(
                item.emojis.map { emojiUi ->
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
