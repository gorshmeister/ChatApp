package ru.gorshenev.themesstyles.domain.model

data class StreamModel(
	val id: Int,
	val name: String,
	val topics: List<TopicModel>,
	val isExpanded: Boolean = false,
) {
	val color: String = topics.firstOrNull()?.color ?: "#2A9D8F"
}