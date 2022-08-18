package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.DateUi


class DateViewHolder(
    view: View,
) : BaseViewHolder<DateUi>(view) {

    val date: TextView = view.findViewById(R.id.tv_date)

    override fun bind(item: DateUi) {
        date.text = item.text
    }
}
