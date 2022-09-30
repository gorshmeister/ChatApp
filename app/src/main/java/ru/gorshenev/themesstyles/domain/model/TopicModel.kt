package ru.gorshenev.themesstyles.domain.model

data class TopicModel(
	val id: Int,
	val name: String,
	val color: String = "#2A9D8F",
)