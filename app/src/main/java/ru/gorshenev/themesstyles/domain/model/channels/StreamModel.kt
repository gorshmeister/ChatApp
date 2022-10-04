package ru.gorshenev.themesstyles.domain.model.channels

data class StreamModel(
    val id: Int,
    val name: String,
    val topics: List<TopicModel>,
    val isExpanded: Boolean = false,
) {
    val color = topics.firstOrNull()?.color ?: "#2A9D8F"
}