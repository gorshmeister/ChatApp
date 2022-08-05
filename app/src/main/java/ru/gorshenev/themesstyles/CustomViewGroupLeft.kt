package ru.gorshenev.themesstyles

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight

class CustomViewGroupLeft @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var ivAvatar: ImageView
    private var tvName: TextView
    private var tvTime: TextView
    private var tvText: TextView
    private var flexbox: FlexboxLayout
    private var background: CardView

    private val avatarRect = Rect()
    private val nameRect = Rect()
    private val timeRect = Rect()
    private val textRect = Rect()
    private val flexboxRect = Rect()
    private val backgroundRect = Rect()

    init {
        LayoutInflater.from(context).inflate(R.layout.view_custom_view_group_left, this, true)
        ivAvatar = findViewById(R.id.ivAvatar)
        tvName = findViewById(R.id.tvName)
        tvTime = findViewById(R.id.tvTime)
        tvText = findViewById(R.id.tvText)
        flexbox = findViewById(R.id.flexbox)
        background = findViewById(R.id.background)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val ivAvatarLayoutParams = ivAvatar.layoutParams as MarginLayoutParams
        val tvNameLayoutParams = tvName.layoutParams as MarginLayoutParams
        val tvTimeLayoutParams = tvTime.layoutParams as MarginLayoutParams
        val tvTextLayoutParams = tvText.layoutParams as MarginLayoutParams
        val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
        val backgroundLayoutParams = background.layoutParams as MarginLayoutParams

        measureChildWithMargins(ivAvatar, widthMeasureSpec, 0, heightMeasureSpec, 0)

        val avatarHeight =
            ivAvatar.measuredHeight + ivAvatarLayoutParams.topMargin + ivAvatarLayoutParams.bottomMargin
        val avatarWidth =
            ivAvatar.measuredWidth + ivAvatarLayoutParams.leftMargin + ivAvatarLayoutParams.rightMargin


        measureChildWithMargins(background, widthMeasureSpec, avatarWidth, heightMeasureSpec, 0)

        val backgroundHeight =
            background.measuredHeight + backgroundLayoutParams.topMargin + backgroundLayoutParams.bottomMargin

        val backgroundWidth =
            background.measuredWidth + backgroundLayoutParams.leftMargin + backgroundLayoutParams.rightMargin



        measureChildWithMargins(tvName, widthMeasureSpec, avatarWidth, heightMeasureSpec, 0)

        val nameHeight =
            tvName.measuredHeight + tvNameLayoutParams.topMargin + tvNameLayoutParams.bottomMargin
        val nameWidth =
            tvName.measuredWidth + tvNameLayoutParams.leftMargin + tvNameLayoutParams.rightMargin



        measureChildWithMargins(
            tvText,
            widthMeasureSpec,
            avatarWidth,
            heightMeasureSpec,
            nameHeight
        )

        val textHeight =
            tvText.measuredHeight + tvTextLayoutParams.topMargin + tvTextLayoutParams.bottomMargin
        val textWidth =
            tvText.measuredWidth + tvTextLayoutParams.leftMargin + tvTextLayoutParams.rightMargin



        measureChildWithMargins(tvTime, widthMeasureSpec, nameWidth, heightMeasureSpec, textHeight)

        val timeHeight =
            tvTime.measuredHeight + tvTimeLayoutParams.topMargin + tvTimeLayoutParams.bottomMargin
        val timeWidth =
            tvTime.measuredWidth + tvTimeLayoutParams.leftMargin + tvTimeLayoutParams.rightMargin



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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val ivAvatarLayoutParams = ivAvatar.layoutParams as MarginLayoutParams
        val nameLayoutParams = tvName.layoutParams as MarginLayoutParams
        val timeLayoutParams = tvTime.layoutParams as MarginLayoutParams
        val textLayoutParams = tvText.layoutParams as MarginLayoutParams
        val flexboxLayoutParams = flexbox.layoutParams as MarginLayoutParams
        val backgroundLayoutParams = background.layoutParams as MarginLayoutParams

        avatarRect.left = ivAvatarLayoutParams.leftMargin + paddingLeft
        avatarRect.top = ivAvatarLayoutParams.topMargin + paddingTop
        avatarRect.right = avatarRect.left + ivAvatar.measuredWidth
        avatarRect.bottom = avatarRect.top + ivAvatar.measuredHeight
        ivAvatar.layout(avatarRect)


        backgroundRect.left =
            background.marginLeft + avatarRect.right + ivAvatarLayoutParams.rightMargin
        backgroundRect.top = backgroundLayoutParams.topMargin
        backgroundRect.right = backgroundRect.left + maxOf(
            tvText.measuredWidth,
            tvName.measuredWidth
        ) + background.paddingLeft + background.paddingRight
        backgroundRect.bottom = backgroundRect.top +
                nameLayoutParams.topMargin + tvName.measuredHeight +
                textLayoutParams.topMargin + tvText.measuredHeight + tvText.marginBottom +
                timeLayoutParams.topMargin + tvTime.measuredHeight
        background.layout(backgroundRect)

        flexboxRect.left = flexbox.marginLeft + avatarRect.right + ivAvatarLayoutParams.rightMargin
        flexboxRect.top = flexboxLayoutParams.topMargin + backgroundRect.bottom
        flexboxRect.right = measuredWidth - flexbox.marginRight
        flexboxRect.bottom = flexboxRect.top + flexbox.measuredHeight + flexbox.marginBottom
        flexbox.layout(flexboxRect)
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