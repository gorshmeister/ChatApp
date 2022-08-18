package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.CustomViewGroupRight
import ru.gorshenev.themesstyles.EmojiView
import ru.gorshenev.themesstyles.FlexboxLayout
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.Utils.px
import ru.gorshenev.themesstyles.Utils.toEmojiString
import ru.gorshenev.themesstyles.items.RightMessageUi

class RightMessageViewHolder(
    view: View,
    private val onMessageClick: ((messageId: Int) -> Unit),
    private val onEmojiClick: (emojiCode: Int, messageId: Int) -> Unit,
) :
    BaseViewHolder<RightMessageUi>(view) {

    val messageView: CustomViewGroupRight = view.findViewById(R.id.view_message)

    val text: TextView = view.findViewById(R.id.tv_msg_text)
    val time: TextView = view.findViewById(R.id.tv_msg_time)
    val flexBox: FlexboxLayout = view.findViewById(R.id.flexbox)

    override fun bind(item: RightMessageUi) {
        messageView.setOnLongClickListener {
            onMessageClick(item.id)
            true
        }
        text.text = item.text
        time.text = item.time

        flexBox.removeAllViews()
        flexBox.addViews(
            item.emojis.map { emojiUi ->
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
