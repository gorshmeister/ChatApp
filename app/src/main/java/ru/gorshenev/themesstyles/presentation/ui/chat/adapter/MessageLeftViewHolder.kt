package ru.gorshenev.themesstyles.presentation.ui.chat.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupLeftBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.ui.chat.items.EmojiUi
import ru.gorshenev.themesstyles.presentation.ui.chat.items.MessageLeftUi
import ru.gorshenev.themesstyles.presentation.ui.chat.views.EmojiView
import ru.gorshenev.themesstyles.utils.Utils.px
import ru.gorshenev.themesstyles.utils.Utils.toEmojiString


class MessageLeftViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiName: String, emojiCode: String, messageId: Int) -> Unit
) : BaseViewHolder<MessageLeftUi>(view) {
    private val binding: ViewCustomViewGroupLeftBinding by viewBinding()

    override fun bind(item: MessageLeftUi) {
        itemView.setOnLongClickListener {
            onMessageClick(item.id)
            true
        }

        with(binding) {
            if (item.avatar == null)
                ivMsgAvatar.setImageResource(R.drawable.ic_launcher_background)
            else
                Glide.with(itemView).load(item.avatar).into(ivMsgAvatar)
            tvMsgName.text = item.name
            tvMsgText.text = item.text
            tvMsgTime.text = item.time

            flexbox.removeAllViews()
            flexbox.addViews(
                item.emojis.map { emojiUi: EmojiUi ->
                    EmojiView(flexbox.context).apply {
                        text = emojiUi.code.toEmojiString()
                        count = emojiUi.counter
                        messageId = emojiUi.msgId
                        userId += emojiUi.listUsersId
                        isSelected = emojiUi.isSelected
                        setOnClickListener {
                            onEmojiClick(emojiUi.name, emojiUi.code.toEmojiString(), item.id)
                        }
                    }
                }
            )
            if (item.emojis.isNotEmpty()) {
                flexbox.addViews(listOf(EmojiView(flexbox.context).apply {
                    text = "+"
                    this.setSize(45.px, 30.px)
                    setOnClickListener { onMessageClick(item.id) }
                }))
            }
        }
    }
}


