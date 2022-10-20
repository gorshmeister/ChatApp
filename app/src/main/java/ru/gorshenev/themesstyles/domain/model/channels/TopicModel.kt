package ru.gorshenev.themesstyles.domain.model.channels

import androidx.annotation.ColorInt
import ru.gorshenev.themesstyles.presentation.ui.channels.StreamFragment
import ru.gorshenev.themesstyles.presentation.ui.channels.items.TopicUi

data class TopicModel(
    val id: Int,
    val name: String,
    @ColorInt val color: Int = TopicUi.DEFAULT_COLOR,
    val type: StreamFragment.StreamType
)