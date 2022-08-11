package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder

class StreamViewHolder(
    view: View,
    private val onStreamClick: (streamId: Int) -> Unit
) : BaseViewHolder<StreamUi>(view) {

    val stream = view.findViewById<TextView>(R.id.tv_stream)

    override fun bind(item: StreamUi) {
        stream.text = item.name

        stream.setOnClickListener { onStreamClick(item.id) }
    }
}