package ru.gorshenev.themesstyles.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.ViewTyped

data class TopicUi(
    override val id: Int,
    val name: String,
    override val viewType: Int = R.layout.item_channels_topic
) : ViewTyped {

}