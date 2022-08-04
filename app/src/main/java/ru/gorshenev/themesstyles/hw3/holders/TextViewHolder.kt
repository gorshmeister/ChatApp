package ru.gorshenev.themesstyles.hw3.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.rv.BaseViewHolder
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.hw3.items.TextUi


class TextViewHolder(
    view: View,
) : BaseViewHolder<TextUi>(view) {

    val textHolder: TextView = view.findViewById(R.id.textHolder)

    override fun bind(item: TextUi) {
        textHolder.text = item.text
    }
}
