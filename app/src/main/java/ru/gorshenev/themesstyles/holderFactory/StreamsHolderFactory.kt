package ru.gorshenev.themesstyles.holderFactory

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.baseRecyclerView.BaseViewHolder
import ru.gorshenev.themesstyles.baseRecyclerView.HolderFactory
import ru.gorshenev.themesstyles.holders.StreamViewHolder
import ru.gorshenev.themesstyles.holders.TopicViewHolder

class StreamsHolderFactory(
    private val onStreamClick: (streamId: Int) -> Unit,
    private val onTopicClick: (topicId: Int) -> Unit
) : HolderFactory() {
    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.component_channels_stream -> StreamViewHolder(view, onStreamClick)
            R.layout.component_channels_topic -> TopicViewHolder(view, onTopicClick)
            else -> null
        }
    }

}
