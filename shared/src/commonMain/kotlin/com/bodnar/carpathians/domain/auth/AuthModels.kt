package com.bodnar.carpathians.domain.auth

/** Авторизований користувач. */
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

/** Дані форми реєстрації. */
data class SignUpData(
    val firstName: String,
    val username: String,
    val email: String,
    val password: String,
)

/** Пара токенів, отримана від сервера. */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)

/** Причини, з яких запит авторизації може не вдатися. */
enum class AuthError {
    /** Неправильне імʼя користувача або пароль. */
    InvalidCredentials,

    /** Немає зʼєднання з сервером або сплив час очікування. */
    Network,

    /** Помилка на боці сервера (5xx). */
    Server,

    /** Сесія закінчилась, і токен не вдалося оновити. */
    SessionExpired,

    Unknown,
}

/** Результат операції авторизації. */
sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Failure(val error: AuthError) : AuthResult<Nothing>
}
