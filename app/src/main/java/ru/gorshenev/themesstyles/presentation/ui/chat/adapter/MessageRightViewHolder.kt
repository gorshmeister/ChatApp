package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.views.EmojiView
import ru.gorshenev.themesstyles.utils.Utils.px
import ru.gorshenev.themesstyles.utils.Utils.toEmojiString
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupRightBinding
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageRightUi

class MessageRightViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiName: String, emojiCode: String, messageId: Int) -> Unit,
) :
    BaseViewHolder<MessageRightUi>(view) {
    private val binding: ViewCustomViewGroupRightBinding by viewBinding()

    override fun bind(item: MessageRightUi) {
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
                        messageId = emojiUi.msgId
                        userId += emojiUi.listUsersId
                        isSelected = emojiUi.isSelected
                        setOnClickListener { onEmojiClick(emojiUi.name, emojiUi.code.toEmojiString(), item.id) }
//                        setOnClickListener { onEmojiClick(emojiUi.code, item.id) }
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
