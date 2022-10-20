package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Narrow(
    val operator: String,
    val operand: String
)