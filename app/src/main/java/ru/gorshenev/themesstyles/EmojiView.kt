package ru.gorshenev.themesstyles

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.view.setPadding

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
//	defStyleRes: Int = R.style.Widget_MyApp_EmojiView,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var tvEmoji: TextView

    var text: String? = null
        set(value) {
            field = value
            tvEmoji.text = text
        }

    @ColorInt
    var textColor: Int = Color.BLACK
        set(value) {
            field = value
            tvEmoji.setTextColor(value)
        }

    var evTextSize: Int = 0
        set(value) {
            field = value
            tvEmoji.textSize = evTextSize.toFloat()
        }

    var evPadding: Int = 0
        set(value) {
            field = value
            tvEmoji.setPadding(evPadding)
        }

    var count = 0
        set(value) {
            field = value
            tvEmoji.text = if (text == "+") {
                text
            } else {
                "$text $count"
            }
        }

    var messageId = 0
        set(value) {
			field = value
		}

	var userId: MutableList<Int> = mutableListOf()
        set(value) {
			field = value
		}

    init {
        inflate(context, R.layout.view_emoji, this)
        tvEmoji = findViewById(R.id.tvEmoji)

        //todo write correct style
//		context.obtainStyledAttributes(attrs, R.styleable.EmojiView, defStyleAttr, defStyleRes)
//			.apply {
//				text = getString(R.styleable.EmojiView_ev_text) ?: "${0x1F643.toEmojiString()} $count"
//				textColor = getColor(R.styleable.EmojiView_ev_textColor, Color.BLACK)
//				evTextSize = getDimensionPixelSize(R.styleable.EmojiView_ev_textSize, 2)
//				evPadding = getDimensionPixelSize(R.styleable.EmojiView_ev_padding, 2)
//				recycle()
//			}
    }


    fun setSize(width: Int, height: Int) {
        tvEmoji.layoutParams = LayoutParams(width, height)
//		requestLayout()
    }

}