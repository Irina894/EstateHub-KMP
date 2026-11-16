package ua.edu.chnu.estatehub.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
    val expiresInMins: Int,
)

/** Відповідь /auth/login та /auth/refresh (зайві поля ігноруються). */
@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
)

/** Відповідь /auth/me. */
@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
)
