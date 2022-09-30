package ru.gorshenev.themesstyles.presentation.ui.channels.items

import androidx.core.graphics.toColorInt
import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base_recycler_view.ViewTyped

data class TopicUi(
    override val id: Int,
    val name: String,
    val color: Int = "#2A9D8F".toColorInt(),
    override val viewType: Int = R.layout.item_channels_topic
) : ViewTyped {

}
