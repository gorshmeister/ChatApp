package ru.gorshenev.themesstyles.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetUserResponse(
    @SerialName("members") val members: List<UserResponse>
)

@Serializable
data class UserResponse(
    @SerialName("user_id") val userId: Int,
    @SerialName("full_name") val firstName: String,
    @SerialName("delivery_email") val email: String,
    @SerialName("timezone") val timeZone: String,
    @SerialName("avatar_url") val avatarUrl: String
)

@Serializable
data class GetOneUserResponse(
    @SerialName("user") val members: UserResponse
)

@Serializable
data class Narrow(
    val operator: String,
    val operand: String
)
