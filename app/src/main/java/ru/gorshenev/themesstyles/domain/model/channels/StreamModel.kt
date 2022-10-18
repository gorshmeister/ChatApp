package ru.gorshenev.themesstyles.domain.model.channels

import androidx.annotation.ColorInt
import ru.gorshenev.themesstyles.R

data class StreamModel(
    val id: Int,
    val name: String,
    val topics: List<TopicModel>
) {
    @ColorInt
    val color = topics.firstOrNull()?.color ?: R.color.color_primary
}