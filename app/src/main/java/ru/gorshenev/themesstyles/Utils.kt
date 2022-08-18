package ru.gorshenev.themesstyles

import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import ru.gorshenev.themesstyles.items.*
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

    fun RecyclerView.setDivider() {
        val divider = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(this.context, R.drawable.line_divider)

        drawable?.let {
            divider.setDrawable(it)
            addItemDecoration(divider)
        }
    }

    fun createStreams(count: Int): List<StreamUi> {
        var id = 0
        var cnt = 1
        return List(count) {

            StreamUi(
                id = ++id,
                name = "#Stream №${cnt}",
                topics = listOf(
                    TopicUi(++id, "Topic №$cnt"),
                    TopicUi(++id, "Topic №${++cnt}"),
                )
            )
        }
    }

    fun createPeople(count: Int): List<PeopleUi> {
        return List(count) {
            PeopleUi(
                id = it,
                name = "Name Surname:$it",
            )
        }
    }

    fun initStreamSearch(cachedItems: Set<ViewTyped>, searchText: String): List<ViewTyped> {
        val digits = searchText.filter { it.isDigit() }

        val regex = when {
            digits.isNotBlank() ->
                "${searchText.replace(digits, "").trim()}.*${digits}$"
                    .toRegex(RegexOption.IGNORE_CASE)

            else -> searchText.trim().toRegex(RegexOption.IGNORE_CASE)
        }

        val filteredItems = cachedItems.filter { item ->
            item is StreamUi && item.name.contains(regex)
                || item is StreamUi && item.topics.any { it.name.contains(regex) } }

        return when (searchText) {
            "" -> cachedItems.toList()
            else -> filteredItems
        }
    }

    fun initUserSearch(cachedItems: Set<ViewTyped>, searchText: String): List<ViewTyped> {
        return when {
            searchText.isNotBlank() -> {
                val digits = searchText.filter { it.isDigit() }

                val regex = when {
                    digits.isNotBlank() ->
                        "${searchText.replace(digits, "").trim()}.*${digits}$"
                            .toRegex(RegexOption.IGNORE_CASE)

                    else -> searchText.trim().toRegex(RegexOption.IGNORE_CASE)
                }

                cachedItems.filter {
                    it is PeopleUi &&
                            (it.name.contains(regex)
                                    || it.email.contains(regex))
                }
            }
            else -> cachedItems.toList()
        }
    }


}