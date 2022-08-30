package ru.gorshenev.themesstyles

import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import ru.gorshenev.themesstyles.baseRecyclerView.ViewTyped
import ru.gorshenev.themesstyles.items.PeopleUi
import ru.gorshenev.themesstyles.items.StreamUi
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

    fun initStreamSearch(cachedItems: List<ViewTyped>, searchText: String): List<ViewTyped> {
        val digits = searchText.filter { it.isDigit() }

        return when {
            digits.isNotEmpty() -> {
                val text = searchText.filter { !it.isDigit() }

                cachedItems.filter { item ->
                    item is StreamUi &&
                            (item.name.contains(text) && item.name.contains(digits) ||
                                    (item.topics.any {
                                        it.name.contains(text) && it.name.contains(
                                            digits
                                        )
                                    }))
                }
            }
            else -> {
                cachedItems.filter { item ->
                    item is StreamUi && (item.name.contains(searchText) ||
                            item.topics.any { it.name.contains(searchText) })
                }
            }
        }
    }

    //todo куда засунуть это
    fun initStreamSearchObservable(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Observable<List<ViewTyped>> {
        val digits = searchText.filter { it.isDigit() }

        return Observable.fromCallable {
            when {
                digits.isNotEmpty() -> {
                    val text = searchText.filter { !it.isDigit() }

                    cachedItems.filter { item ->
                        item is StreamUi &&
                                (item.name.contains(text, true) && item.name.contains(
                                    digits,
                                    true
                                ) ||
                                        (item.topics.any {
                                            it.name.contains(text, true) && it.name.contains(
                                                digits,
                                                true
                                            )
                                        }))
                    }
                }
                searchText.isNotEmpty() -> {
                    cachedItems.filter { item ->
                        item is StreamUi && (item.name.contains(searchText, true) ||
                                item.topics.any { it.name.contains(searchText, true) })
                    }
                }
                else -> cachedItems
            }
        }
    }

    fun initUserSearchObservable(
        cachedItems: List<ViewTyped>,
        searchText: String
    ): Observable<List<ViewTyped>> {
        return Observable.fromCallable {
            return@fromCallable when {
                searchText.isEmpty() -> cachedItems
                else -> cachedItems.filter { item ->
                    item is PeopleUi && (item.name.contains(searchText, true) || item.email.contains(searchText, true))
                }

            }
        }
    }

    fun initUserSearch(cachedItems: List<ViewTyped>, searchText: String): List<ViewTyped> {
        return when {
            searchText.isNotBlank() -> {
                val digits = searchText.filter { it.isDigit() }

                val regex = when {

                    digits.isNotBlank() -> searchText
                        .replace(digits, "")
                        .trim().plus(".*${digits}$")

                    else -> searchText.trim()

                }.toRegex(RegexOption.IGNORE_CASE)

                cachedItems.filter { item ->
                    item is PeopleUi && (item.name.contains(regex) || item.email.contains(regex))
                }
            }
            else -> cachedItems.toList()
        }
    }
}

