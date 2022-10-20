package ru.gorshenev.themesstyles.presentation.ui.channels.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.databinding.ItemChannelsStreamBinding
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.ui.channels.items.StreamUi

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