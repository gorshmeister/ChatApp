package ru.gorshenev.themesstyles.domain.model.channels

import androidx.annotation.ColorInt
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment

data class TopicModel(
    val id: Int,
    val name: String,
    @ColorInt val color: Int = 0,
    val type: StreamFragment.StreamType
)