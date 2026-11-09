package com.bodnar.carpathians.domain.auth.validation

/** Помилка валідації поля форми входу. */
sealed interface ValidationError {
    data object Empty : ValidationError
    data class TooShort(val minLength: Int) : ValidationError
    data object InvalidCharacters : ValidationError
    data object ContainsSpaces : ValidationError
}

/**
 * Ім'я користувача: не порожнє, щонайменше [MIN_LENGTH] символи,
 * лише латинські літери, цифри та . _ -
 */
class ValidateUsernameUseCase {
    operator fun invoke(username: String): ValidationError? {
        val value = username.trim()
        return when {
            value.isEmpty() -> ValidationError.Empty
            value.length < MIN_LENGTH -> ValidationError.TooShort(MIN_LENGTH)
            !USERNAME_REGEX.matches(value) -> ValidationError.InvalidCharacters
            else -> null
        }
    }

    companion object {
        const val MIN_LENGTH = 3
        private val USERNAME_REGEX = Regex("^[A-Za-z0-9._-]+$")
    }
}

/** Пароль: не порожній, без пробілів, щонайменше [MIN_LENGTH] символів. */
class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationError? = when {
        password.isEmpty() -> ValidationError.Empty
        password.any { it.isWhitespace() } -> ValidationError.ContainsSpaces
        password.length < MIN_LENGTH -> ValidationError.TooShort(MIN_LENGTH)
        else -> null
    }

    companion object {
        const val MIN_LENGTH = 6
    }
}
