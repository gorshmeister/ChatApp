package ru.gorshenev.themesstyles.hw3

import android.content.res.Resources
import ru.gorshenev.themesstyles.hw3.items.EmojiUi
import kotlin.math.roundToInt

object Utils {

    fun Int.toEmojiString(): String =
        if (this == EmojiUi.PLUS_CODE) "+" else String(Character.toChars(this))

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()


}