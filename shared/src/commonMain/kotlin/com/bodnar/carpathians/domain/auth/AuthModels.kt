package com.bodnar.carpathians.domain.auth

data class User(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
) {
    val fullName: String get() = "$firstName $lastName".trim()
    val initials: String
        get() = listOf(firstName, lastName)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifEmpty { username.take(1).uppercase() }
}

data class SignUpData(
    val firstName: String,
    val username: String,
    val email: String,
    val password: String,
)

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

enum class AuthError {
    InvalidCredentials,

    Network,

    Server,

    SessionExpired,

    Unknown,
}

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Failure(val error: AuthError) : AuthResult<Nothing>
}
