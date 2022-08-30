package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.StreamUi
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemChannelsStreamBinding

class StreamViewHolder(
    view: View,
    private val onStreamClick: (streamId: Int) -> Unit
) : BaseViewHolder<StreamUi>(view) {
    private val binding: ItemChannelsStreamBinding by viewBinding()

    private var currentId: Int? = null

    init {
        itemView.setOnClickListener {
            currentId?.let(onStreamClick)
        }
    }
    override fun bind(item: StreamUi) {
        currentId = item.id
        binding.tvStream.text = item.name
        binding.ivStream.isEnabled = item.isExpanded
    }
}