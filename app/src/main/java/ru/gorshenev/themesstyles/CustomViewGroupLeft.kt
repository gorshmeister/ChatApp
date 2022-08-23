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
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.databinding.ViewCustomViewGroupLeftBinding

class CustomViewGroupLeft @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: ViewCustomViewGroupLeftBinding by viewBinding()

    private val avatarRect = Rect()
    private val nameRect = Rect()
    private val timeRect = Rect()
    private val textRect = Rect()
    private val flexboxRect = Rect()
    private val backgroundRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_custom_view_group_left, this, true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        with(binding) {
            val ivAvatarLayoutParams = ivMsgAvatar.layoutParams as MarginLayoutParams
            val tvNameLayoutParams = tvMsgName.layoutParams as MarginLayoutParams
            val tvTimeLayoutParams = tvMsgTime.layoutParams as MarginLayoutParams
            val tvTextLayoutParams = tvMsgText.layoutParams as MarginLayoutParams
            val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
            val backgroundLayoutParams = background.layoutParams as MarginLayoutParams

            measureChildWithMargins(ivMsgAvatar, widthMeasureSpec, 0, heightMeasureSpec, 0)

            val avatarHeight =
                ivMsgAvatar.measuredHeight + ivAvatarLayoutParams.topMargin + ivAvatarLayoutParams.bottomMargin
            val avatarWidth =
                ivMsgAvatar.measuredWidth + ivAvatarLayoutParams.leftMargin + ivAvatarLayoutParams.rightMargin


            measureChildWithMargins(background, widthMeasureSpec, avatarWidth, heightMeasureSpec, 0)

            val backgroundHeight =
                background.measuredHeight + backgroundLayoutParams.topMargin + backgroundLayoutParams.bottomMargin

            val backgroundWidth =
                background.measuredWidth + backgroundLayoutParams.leftMargin + backgroundLayoutParams.rightMargin



            measureChildWithMargins(tvMsgName, widthMeasureSpec, avatarWidth, heightMeasureSpec, 0)

            val nameHeight =
                tvMsgName.measuredHeight + tvNameLayoutParams.topMargin + tvNameLayoutParams.bottomMargin
            val nameWidth =
                tvMsgName.measuredWidth + tvNameLayoutParams.leftMargin + tvNameLayoutParams.rightMargin



            measureChildWithMargins(
                tvMsgText,
                widthMeasureSpec,
                avatarWidth,
                heightMeasureSpec,
                nameHeight
            )

            val textHeight =
                tvMsgText.measuredHeight + tvTextLayoutParams.topMargin + tvTextLayoutParams.bottomMargin
            val textWidth =
                tvMsgText.measuredWidth + tvTextLayoutParams.leftMargin + tvTextLayoutParams.rightMargin



            measureChildWithMargins(
                tvMsgTime,
                widthMeasureSpec,
                nameWidth,
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
                avatarWidth,
                heightMeasureSpec,
                textHeight
            )

            val flexboxHeight =
                flexbox.measuredHeight + flexboxLayoutParams.topMargin + flexboxLayoutParams.bottomMargin
            val flexboxWidth =
                flexbox.measuredWidth + flexboxLayoutParams.leftMargin + flexboxLayoutParams.rightMargin


            val height = maxOf(avatarHeight, nameHeight + textHeight + timeHeight + flexboxHeight)
            setMeasuredDimension(
                resolveSize(avatarWidth + backgroundWidth, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec)
            )
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        with(binding) {
            val ivAvatarLayoutParams = ivMsgAvatar.layoutParams as MarginLayoutParams
            val nameLayoutParams = tvMsgName.layoutParams as MarginLayoutParams
            val timeLayoutParams = tvMsgTime.layoutParams as MarginLayoutParams
            val textLayoutParams = tvMsgText.layoutParams as MarginLayoutParams
            val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
            val backgroundLayoutParams = background.layoutParams as MarginLayoutParams

            avatarRect.left = ivAvatarLayoutParams.leftMargin + paddingLeft
            avatarRect.top = ivAvatarLayoutParams.topMargin + paddingTop
            avatarRect.right = avatarRect.left + ivMsgAvatar.measuredWidth
            avatarRect.bottom = avatarRect.top + ivMsgAvatar.measuredHeight
            ivMsgAvatar.layout(avatarRect)


            backgroundRect.left =
                background.marginLeft + avatarRect.right + ivAvatarLayoutParams.rightMargin
            backgroundRect.top = backgroundLayoutParams.topMargin
            backgroundRect.right = backgroundRect.left + maxOf(
                tvMsgText.measuredWidth,
                tvMsgName.measuredWidth
            ) + background.paddingLeft + background.paddingRight
            backgroundRect.bottom = backgroundRect.top +
                    nameLayoutParams.topMargin + tvMsgName.measuredHeight +
                    textLayoutParams.topMargin + tvMsgText.measuredHeight + tvMsgText.marginBottom +
                    timeLayoutParams.topMargin + tvMsgTime.measuredHeight
            background.layout(backgroundRect)

            flexboxRect.left =
                flexbox.marginLeft + avatarRect.right + ivAvatarLayoutParams.rightMargin
            flexboxRect.top = flexboxLayoutParams.topMargin + backgroundRect.bottom
            flexboxRect.right = measuredWidth - flexbox.marginRight
            flexboxRect.bottom = flexboxRect.top + flexbox.measuredHeight + flexbox.marginBottom
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