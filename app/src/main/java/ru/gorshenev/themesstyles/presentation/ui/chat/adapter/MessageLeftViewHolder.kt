package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.views.EmojiView
import ru.gorshenev.themesstyles.data.Utils.px
import ru.gorshenev.themesstyles.data.Utils.toEmojiString
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupLeftBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi


class MessageLeftViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit
) : BaseViewHolder<MessageLeftUi>(view) {
    private val binding: ViewCustomViewGroupLeftBinding by viewBinding()

    override fun bind(item: MessageLeftUi) {
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


