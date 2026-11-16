package com.bodnar.carpathians.domain.auth.validation

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

class ValidatePasswordConfirmationUseCase {
    operator fun invoke(password: String, confirmation: String): ValidationError? = when {
        confirmation.isEmpty() -> ValidationError.Empty
        confirmation != password -> ValidationError.PasswordsDoNotMatch
        else -> null
    }
}

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
