package ru.gorshenev.themesstyles

import android.content.res.Resources
import ru.gorshenev.themesstyles.items.EmojiUi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun Int.toEmojiString(): String = String(Character.toChars(this))

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("kk:mm", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }

    fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }
}