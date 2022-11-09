package ru.gorshenev.themesstyles.presentation.ui.channels.items

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.base.recycler_view.ViewTyped

data class TopicUi(
    override val id: Int,
    val name: String,
    val color: Int = DEFAULT_COLOR,
    val streamId: Int,
    override val viewType: Int = R.layout.item_channels_topic
) : ViewTyped {

    companion object {
        const val DEFAULT_COLOR = 0
    }
}
