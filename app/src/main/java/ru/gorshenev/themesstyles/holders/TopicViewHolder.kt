package ru.gorshenev.themesstyles.holders

import android.view.View
import android.widget.TextView
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.items.TopicUi
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder

class TopicViewHolder(
    view: View,
    private val onTopicClick: (topicId: Int) -> Unit
) : BaseViewHolder<TopicUi>(view) {

    val topic = view.findViewById<TextView>(R.id.tv_topic)

    override fun bind(item: TopicUi) {
        topic.text = item.name

        topic.setOnClickListener { onTopicClick(item.id) }
    }
}