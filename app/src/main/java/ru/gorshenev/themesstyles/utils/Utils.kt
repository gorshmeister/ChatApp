package ru.gorshenev.themesstyles.utils

import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Single
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped
import ru.gorshenev.themesstyles.presentation.ui.people.items.PeopleUi
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object Utils {

    fun Int.toEmojiString(): String = String(Character.toChars(this))

    fun String.toEmojiString(): String = String(Character.toChars(this.toInt(16)))

    fun String.toEmojiCode(): Int = Integer.parseInt(this, 16)

    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat("kk:mm", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }
    fun getTimeFromUnix(time: Long): String {
        val formatter = SimpleDateFormat("kk:mm", Locale.getDefault())
        return formatter.format(time*1000)
    }

    fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
        val current = Calendar.getInstance().time
        return formatter.format(current)
    }
    fun getDateFromUnix(time: Long): String {
        val formatter = SimpleDateFormat("d MMM", Locale.getDefault())
//        val current = Calendar.getInstance().time
        return formatter.format(time*1000)
    }

    fun RecyclerView.setDivider() {
        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(this.context, R.drawable.line_divider)

        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }
}

