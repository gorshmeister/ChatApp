package ru.gorshenev.themesstyles

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Px
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val circlePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.FILL_AND_STROKE
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
    }

    private var foregroundDrawable: Drawable? = null
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private var textSize: Int
        get() = textPaint.textSize.toInt()
        set(value) {
            textPaint.textSize = value.toFloat()
            requestLayout()
        }

    private var text: String = TEXT
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }

    private val centerPoint = PointF()
    private val textPoint = PointF()
    private var radius: Float = 0F
    private val textBounds = Rect()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CircleImageView).apply {
            textSize =
                getDimensionPixelSize(R.styleable.CircleImageView_cl_text_size, 200)
            foregroundDrawable = getDrawable(R.styleable.CircleImageView_cl_foreground)
            text = getText(R.styleable.CircleImageView_cl_text)?.toString() ?: TEXT
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(text, 0, text.length, textBounds)

        val textWidth = textBounds.width()
        val textHeight = textBounds.height()
        val contentWidth = textWidth + paddingStart + paddingEnd
        val contentHeight = textHeight + paddingTop + paddingBottom
        val contentSize = maxOf(contentWidth, contentHeight)

        val mode = MeasureSpec.getMode(widthMeasureSpec)
        val specSize = MeasureSpec.getSize(widthMeasureSpec)
        radius = when (mode) {
            MeasureSpec.EXACTLY -> specSize / 2f
            MeasureSpec.AT_MOST -> {
                val radius = contentSize * sqrt(2f) / 2
                if (radius < specSize) radius else specSize / 2f
            }
            else -> contentSize * sqrt(2f) / 2
        }

        val size = (radius * 2).roundToInt()
        setMeasuredDimension(size, size)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        centerPoint.set(radius, radius)
        val emptySpaceVertical = height - textBounds.height()
        textPoint.set(
            radius,
            emptySpaceVertical / 2f + textBounds.height()

        )
    }

    override fun onDraw(canvas: Canvas) {
        val canvasCount = canvas.save()
//        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, circlePaint)
        canvas.drawText(text, textPoint.x, textPoint.y, textPaint)
        canvas.restoreToCount(canvasCount)
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isSelected)
            mergeDrawableStates(drawableState, DRAWABLES_STATE)
        return drawableState
    }

    override fun performClick(): Boolean {
        isSelected = !isSelected
        return super.performClick()
    }

    companion object {
        private const val TEXT = "Hi"
//        private const val DEFAULT_FONT_SIZE_PX = 14F

        private val DRAWABLES_STATE = IntArray(1) { android.R.attr.state_selected }
    }
}

@Px
private fun Context.spToPx(sp: Float): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}
