package ru.gorshenev.themesstyles.presentation.ui.channels.adapter

import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.databinding.ItemChannelsTopicBinding
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
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
        if (item.color == TopicUi.DEFAULT_COLOR) itemView.setBackgroundResource(R.color.color_primary)
        else itemView.setBackgroundColor(item.color)
        binding.tvTopic.text = item.name
        currentId = item.id
    }
}