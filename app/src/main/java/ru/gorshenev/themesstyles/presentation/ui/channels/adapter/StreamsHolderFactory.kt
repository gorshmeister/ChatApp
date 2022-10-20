package ru.gorshenev.themesstyles.presentation.ui.channels.adapter

import android.view.View
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.BaseViewHolder
import ru.gorshenev.themesstyles.presentation.base.recycler_view.HolderFactory

class StreamsHolderFactory(
    private val onStreamClick: (streamId: Int) -> Unit,
    private val onTopicClick: (topicId: Int) -> Unit
) : HolderFactory() {
    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_channels_stream -> StreamViewHolder(view, onStreamClick)
            R.layout.item_channels_topic -> TopicViewHolder(view, onTopicClick)
            else -> null
        }
    }

}
