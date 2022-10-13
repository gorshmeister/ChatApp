package ru.gorshenev.themesstyles.presentation.ui.channels.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class TopicUi(
    override val id: Int,
    val name: String,
    val color: String = R.color.color_primary.toString(),
    override val viewType: Int = R.layout.item_channels_topic
) : ViewTyped {

}
