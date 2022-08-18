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

    private var currentId:Int? = null

    init {
        itemView.setOnClickListener{
            currentId?.let(onTopicClick)
        }
    }

    override fun bind(item: TopicUi) {
        topic.text = item.name
        currentId = item.id
    }
}