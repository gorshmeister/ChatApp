package ru.gorshenev.themesstyles.domain.model.channels

import ru.gorshenev.themesstyles.R
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

data class TopicModel(
    val id: Int,
    val name: String,
    val color: String = R.color.color_primary.toString(),
    val type: StreamFragment.StreamType
)