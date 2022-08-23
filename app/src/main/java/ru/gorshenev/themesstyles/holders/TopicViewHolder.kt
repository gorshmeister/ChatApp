package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.TopicUi
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.databinding.ItemChannelsTopicBinding

class TopicViewHolder(
    view: View,
    private val onTopicClick: (topicId: Int) -> Unit
) : BaseViewHolder<TopicUi>(view) {
    private val binding: ItemChannelsTopicBinding by viewBinding()

    private var currentId:Int? = null

    init {
        itemView.setOnClickListener{
            currentId?.let(onTopicClick)
        }
    }

    override fun bind(item: TopicUi) {
        binding.tvTopic.text = item.name
        currentId = item.id
    }
}