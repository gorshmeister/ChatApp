package ru.gorshenev.themesstyles

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

class FlexboxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = R.style.Widget_MyApp_Flexbox,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var gap = 1

    init {
        context.obtainStyledAttributes(attrs, R.styleable.FlexboxLayout, defStyleAttr, defStyleRes)
            .apply {
                gap = getDimensionPixelSize(R.styleable.FlexboxLayout_gap, 10)
                recycle()
            }
        setWillNotDraw(true)
//		repeat(6) {
//			addView(EmojiView(context))
//		}
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		children.forEachIndexed { index, it ->
			measureChildWithMargins(it, widthMeasureSpec, 0, heightMeasureSpec, 0)
//			if ((it as EmojiView).text == "+") {
//                it.text = "11111"
////				todo EmojiView with "+" size
//				val lastEmojiWidth = getChildAt(0).measuredWidth
//				it.setSize(70, it.measuredHeight)
//			}
		}

        var widthOffset = 0
        var numberOfLine = 0
        var widthLine = 0
        var counter = 0

        children.forEachIndexed { index, it ->
            if (counter > 4) {
                widthLine = widthOffset
                counter = 1
                widthOffset = 0
                numberOfLine++
                it.setCoordinates(
                    left = widthOffset,
                    top = it.measuredHeight * numberOfLine + (gap * numberOfLine),
                    right = it.measuredWidth + widthOffset,
                    bottom = it.measuredHeight * (numberOfLine + 1) + (gap * numberOfLine)
                )
            } else {
                counter++
                it.setCoordinates(
                    left = widthOffset,
                    top = it.measuredHeight * numberOfLine + (gap * numberOfLine),
                    right = it.measuredWidth + widthOffset,
                    bottom = it.measuredHeight * (numberOfLine + 1) + (gap * numberOfLine)
                )
            }
            widthOffset += it.measuredWidth + gap
		}

        val childrenHeight = children.map { it.measuredHeight }.maxOrNull() ?: 0
        val height = childrenHeight * (numberOfLine + 1) + (gap * numberOfLine)

        setMeasuredDimension(
            resolveSize(if (widthLine != 0) widthLine else widthOffset, widthMeasureSpec),
            resolveSize(height, heightMeasureSpec),
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        children.forEach {
            it.layout(it.left, it.top, it.right, it.bottom)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.setCoordinates(left: Int, top: Int, right: Int, bottom: Int) {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
    }

    fun addViews(list: List<EmojiView>) {
        list.forEach { view ->
            addView(view)
        }
    }
}