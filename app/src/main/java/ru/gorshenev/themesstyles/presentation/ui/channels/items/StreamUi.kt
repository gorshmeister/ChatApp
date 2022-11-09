package ru.gorshenev.themesstyles.presentation.ui.channels.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

data class StreamUi(
    override val id: Int,
    val name: String,
    val topics: List<TopicUi>,
    val isExpanded: Boolean = false,
    override val viewType: Int = R.layout.item_channels_stream
) : ViewTyped
