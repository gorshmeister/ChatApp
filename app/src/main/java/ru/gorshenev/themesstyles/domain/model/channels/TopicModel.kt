package ru.gorshenev.themesstyles.domain.model.channels

import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

data class TopicModel(
    val id: Int,
    val name: String,
    val color: String = "#2A9D8F",
    val type: StreamFragment.StreamType
)