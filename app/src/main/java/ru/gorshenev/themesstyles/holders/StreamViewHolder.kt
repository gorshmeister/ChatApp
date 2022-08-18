package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder

class StreamViewHolder(
    view: View,
    private val onStreamClick: (streamId: Int) -> Unit
) : BaseViewHolder<StreamUi>(view) {

    val stream: TextView = view.findViewById(R.id.tv_stream)
    val image: ImageView = view.findViewById(R.id.iv_stream_arrow)

    private var currentId: Int? = null

    init {
        itemView.setOnClickListener {
            currentId?.let(onStreamClick)
        }
    }
    override fun bind(item: StreamUi) {
        currentId = item.id
        stream.text = item.name
        image.isEnabled = item.isExpanded
    }
}