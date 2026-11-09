package com.bodnar.carpathians.domain.auth.validation

/** Помилка валідації поля форми входу / реєстрації. */
sealed interface ValidationError {
    data object Empty : ValidationError
    data class TooShort(val minLength: Int) : ValidationError
    data object InvalidCharacters : ValidationError
    data object ContainsSpaces : ValidationError
    data object InvalidName : ValidationError
    data object InvalidEmail : ValidationError
    data object PasswordsDoNotMatch : ValidationError
    data object InvalidPhone : ValidationError
    data class OutOfRange(val min: Int, val max: Int) : ValidationError
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

/** Ім'я людини: не порожнє, щонайменше 2 символи, літери (будь-якої абетки), пробіл, апостроф, дефіс. */
class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationError? {
        val value = name.trim()
        return when {
            value.isEmpty() -> ValidationError.Empty
            value.length < MIN_LENGTH -> ValidationError.TooShort(MIN_LENGTH)
            !value.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' || it == 'ʼ' } ->
                ValidationError.InvalidName
            else -> null
        }
    }

    companion object {
        const val MIN_LENGTH = 2
    }
}

/** Email у форматі name@domain.tld */
class ValidateEmailUseCase {
    operator fun invoke(email: String): ValidationError? {
        val value = email.trim()
        return when {
            value.isEmpty() -> ValidationError.Empty
            !EMAIL_REGEX.matches(value) -> ValidationError.InvalidEmail
            else -> null
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}

/** Повтор пароля має збігатися з паролем. */
class ValidatePasswordConfirmationUseCase {
    operator fun invoke(password: String, confirmation: String): ValidationError? = when {
        confirmation.isEmpty() -> ValidationError.Empty
        confirmation != password -> ValidationError.PasswordsDoNotMatch
        else -> null
    }
}

/** Телефон: 10–13 цифр, можна з "+" на початку та пробілами / дужками / дефісами. */
class ValidatePhoneUseCase {
    operator fun invoke(phone: String): ValidationError? {
        val value = phone.trim()
        val digits = value.count { it.isDigit() }
        val allowed = value.withIndex().all { (i, c) -> c.isDigit() || c in " ()-" || (c == '+' && i == 0) }
        return when {
            value.isEmpty() -> ValidationError.Empty
            !allowed || digits !in 10..13 -> ValidationError.InvalidPhone
            else -> null
        }
    }
}

/** Кількість осіб у групі: ціле число від [MIN] до [MAX]. */
class ValidatePeopleCountUseCase {
    operator fun invoke(count: String): ValidationError? {
        val value = count.trim()
        val number = value.toIntOrNull()
        return when {
            value.isEmpty() -> ValidationError.Empty
            number == null || number !in MIN..MAX -> ValidationError.OutOfRange(MIN, MAX)
            else -> null
        }
    }

    companion object {
        const val MIN = 1
        const val MAX = 20
    }
}
