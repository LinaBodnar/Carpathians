package com.bodnar.carpathians.ui.auth

import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.validation.ValidationError

data class AuthState(
    val username: String = "",
    val password: String = "",
    val usernameError: ValidationError? = null,
    val passwordError: ValidationError? = null,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    /** Помилки показуються після першої спроби входу, далі — під час введення. */
    val showValidation: Boolean = false,
)

enum class AuthField { Username, Password }

sealed interface AuthIntent {
    data class UsernameChanged(val value: String) : AuthIntent
    data class PasswordChanged(val value: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent
    data object SignIn : AuthIntent
}

sealed interface AuthEffect {
    data class FocusField(val field: AuthField) : AuthEffect
    data class ShowError(val error: AuthError) : AuthEffect
    data object NavigateToHome : AuthEffect
}
