package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.CustomViewGroupLeft
import ru.gorshenev.themesstyles.EmojiView
import ru.gorshenev.themesstyles.FlexboxLayout
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.px
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.items.EmojiUi
import ru.gorshenev.themesstyles.items.LeftMessageUi


class LeftMessageViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit
) : BaseViewHolder<LeftMessageUi>(view) {

    val messageView: CustomViewGroupLeft = view.findViewById(R.id.view_message)

    val avatar: ImageView = view.findViewById(R.id.ivAvatar)
    val name: TextView = view.findViewById(R.id.tvName)
    val text: TextView = view.findViewById(R.id.tvText)
    val time: TextView = view.findViewById(R.id.tvTime)
    val flexBox: FlexboxLayout = view.findViewById(R.id.flexbox)


    override fun bind(item: LeftMessageUi) {
        messageView.setOnLongClickListener {
            onMessageClick(item.id)
            true
        }

        avatar.setImageResource(item.avatar)
        name.text = item.name
        text.text = item.text
        time.text = item.time

        flexBox.removeAllViews()
        flexBox.addViews(
            item.emojis.map { emojiUi: EmojiUi ->
                EmojiView(flexBox.context).apply {
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
            flexBox.addViews(listOf(EmojiView(flexBox.context).apply {
                text = "+"
                this.setSize(48.px, 29.px)
                setOnClickListener { onMessageClick(item.id) }
            }))
        }
    }
}


