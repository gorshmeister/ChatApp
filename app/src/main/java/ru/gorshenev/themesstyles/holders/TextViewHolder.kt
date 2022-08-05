package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.TextUi


class TextViewHolder(
    view: View,
) : BaseViewHolder<TextUi>(view) {

    val textHolder: TextView = view.findViewById(R.id.textHolder)

    override fun bind(item: TextUi) {
        textHolder.text = item.text
    }
}
