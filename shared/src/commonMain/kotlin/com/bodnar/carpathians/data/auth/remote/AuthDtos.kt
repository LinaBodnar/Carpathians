package com.bodnar.carpathians.data.auth.remote

import com.bodnar.carpathians.domain.auth.AuthTokens
import com.bodnar.carpathians.domain.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int,
)

@Serializable
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
    val expiresInMins: Int,
)

@Serializable
data class TokensDto(
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class RegisterRequestDto(
    val firstName: String,
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
)

@Serializable
data class ErrorDto(val message: String = "")

fun LoginResponseDto.toUser() = User(id, username, firstName, lastName, email)
fun LoginResponseDto.toTokens() = AuthTokens(accessToken, refreshToken)
fun TokensDto.toTokens() = AuthTokens(accessToken, refreshToken)
fun UserDto.toUser() = User(id, username, firstName, lastName, email)
