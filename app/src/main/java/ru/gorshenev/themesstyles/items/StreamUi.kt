package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.ViewTyped

data class StreamUi(
    override val id: Int,
    val name: String,
    val topics: List<TopicUi>,
    var isExpanded: Boolean = false,
    override val viewType: Int = R.layout.component_channels_stream
) : ViewTyped{

}
