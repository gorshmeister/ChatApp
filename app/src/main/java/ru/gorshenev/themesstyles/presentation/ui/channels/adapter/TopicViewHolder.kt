package ru.gorshenev.themesstyles.presentation.ui.channels.adapter

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ItemChannelsTopicBinding
import ru.gorshenev.themesstyles.presentation.base_recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

class TopicViewHolder(
    view: View,
    private val onTopicClick: (topicId: Int) -> Unit
) : BaseViewHolder<TopicUi>(view) {
    private val binding: ItemChannelsTopicBinding by viewBinding()

    private var currentId: Int? = null

    init {
        itemView.setOnClickListener {
            currentId?.let(onTopicClick)
        }
    }

    override fun bind(item: TopicUi) {
        itemView.setBackgroundColor(item.color)
        binding.tvTopic.text = item.name
        currentId = item.id
    }
}