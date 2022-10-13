package ru.gorshenev.themesstyles.presentation.ui.chat.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.setPadding
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ViewEmojiBinding

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
	defStyleRes: Int = R.style.Widget_MyApp_EmojiView,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: ViewEmojiBinding by viewBinding()

    var messageId = 0

    var userId: MutableList<Int> = mutableListOf()

    var text: String = ""
        set(value) {
            field = value
            binding.tvEmoji.text = text
        }

    @ColorInt
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            binding.tvEmoji.setTextColor(value)
        }

    var count = 0
        set(value) {
            field = value
            binding.tvEmoji.text = context.getString(R.string.emoji_text, text, count)
        }


    init {
        inflate(context, R.layout.view_emoji, this)
    }

    fun setSize(width: Int, height: Int) {
        binding.tvEmoji.layoutParams = LayoutParams(width, height)
    }
}