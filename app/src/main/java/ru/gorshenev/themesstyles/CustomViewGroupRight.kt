package ru.gorshenev.themesstyles

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupRightBinding

class CustomViewGroupRight @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: ViewCustomViewGroupRightBinding by viewBinding()

    private val timeRect = Rect()
    private val textRect = Rect()
    private val flexboxRect = Rect()
    private val backgroundRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_custom_view_group_right, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        with(binding) {
            val tvTimeLayoutParams = tvMsgTime.layoutParams as MarginLayoutParams
            val tvTextLayoutParams = tvMsgText.layoutParams as MarginLayoutParams
            val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
            val backgroundLayoutParams = background.layoutParams as MarginLayoutParams


            measureChildWithMargins(background, widthMeasureSpec, 0, heightMeasureSpec, 0)

            val backgroundHeight =
                background.measuredHeight + backgroundLayoutParams.topMargin + backgroundLayoutParams.bottomMargin

            val backgroundWidth =
                background.measuredWidth + backgroundLayoutParams.leftMargin + backgroundLayoutParams.rightMargin



            measureChildWithMargins(tvMsgText, widthMeasureSpec, 0, heightMeasureSpec, 0)

            val textHeight =
                tvMsgText.measuredHeight + tvTextLayoutParams.topMargin + tvTextLayoutParams.bottomMargin
            val textWidth =
                tvMsgText.measuredWidth + tvTextLayoutParams.leftMargin + tvTextLayoutParams.rightMargin



            measureChildWithMargins(
                tvMsgTime,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                textHeight
            )

            val timeHeight =
                tvMsgTime.measuredHeight + tvTimeLayoutParams.topMargin + tvTimeLayoutParams.bottomMargin
            val timeWidth =
                tvMsgTime.measuredWidth + tvTimeLayoutParams.leftMargin + tvTimeLayoutParams.rightMargin



            measureChildWithMargins(
                flexbox,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                textHeight
            )

            val flexboxHeight =
                flexbox.measuredHeight + flexboxLayoutParams.topMargin + flexboxLayoutParams.bottomMargin
            val flexboxWidth =
                flexbox.measuredWidth + flexboxLayoutParams.leftMargin + flexboxLayoutParams.rightMargin


            val height = maxOf(backgroundHeight, textHeight + timeHeight + flexboxHeight)
            setMeasuredDimension(
                resolveSize(backgroundWidth, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        with(binding) {
            val timeLayoutParams = tvMsgTime.layoutParams as MarginLayoutParams
            val textLayoutParams = tvMsgText.layoutParams as MarginLayoutParams
            val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
            val backgroundLayoutParams = background.layoutParams as MarginLayoutParams

            backgroundRect.top = backgroundLayoutParams.topMargin
            backgroundRect.right = measuredWidth - backgroundLayoutParams.rightMargin
            backgroundRect.left = backgroundRect.right - maxOf(
                tvMsgText.measuredWidth,
                tvMsgTime.measuredWidth
            ) - background.paddingLeft - background.paddingRight
            backgroundRect.bottom =
                backgroundRect.top + tvMsgTime.measuredHeight + tvMsgTime.marginTop + tvMsgText.measuredHeight + tvMsgText.marginTop
            background.layout(backgroundRect)

            flexboxRect.top =
                flexboxLayoutParams.topMargin + backgroundRect.bottom + background.marginBottom
            flexboxRect.right = measuredWidth - flexboxLayoutParams.rightMargin
            flexboxRect.left = flexboxRect.right - flexbox.measuredWidth
            flexboxRect.bottom =
                flexboxRect.top + flexbox.measuredHeight + flexbox.marginBottom
            flexbox.layout(flexboxRect)
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)

    private fun View.layout(rect: Rect) {
        layout(rect.left, rect.top, rect.right, rect.bottom)
    }
}