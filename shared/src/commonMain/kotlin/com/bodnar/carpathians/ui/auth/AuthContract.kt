package com.bodnar.carpathians.ui.auth

import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.validation.ValidationError

/** Вкладки форми, як у дизайні (.glass-login-modal): "Вхід" / "Реєстрація". */
enum class AuthMode { SignIn, SignUp }

enum class AuthField { FirstName, Username, Email, Password, ConfirmPassword }

data class AuthState(
    val mode: AuthMode = AuthMode.SignIn,
    val values: Map<AuthField, String> = emptyMap(),
    val errors: Map<AuthField, ValidationError> = emptyMap(),
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    /** Помилки показуються після першої спроби відправити форму, далі — під час введення. */
    val showValidation: Boolean = false,
) {
    fun value(field: AuthField): String = values[field].orEmpty()

    /** Поля поточної вкладки в порядку на екрані (і в порядку переходу клавіатурою). */
    val fields: List<AuthField>
        get() = when (mode) {
            AuthMode.SignIn -> listOf(AuthField.Username, AuthField.Password)
            AuthMode.SignUp -> listOf(
                AuthField.FirstName,
                AuthField.Username,
                AuthField.Email,
                AuthField.Password,
                AuthField.ConfirmPassword,
            )
        }
}

sealed interface AuthIntent {
    data class ModeChanged(val mode: AuthMode) : AuthIntent
    data class FieldChanged(val field: AuthField, val value: String) : AuthIntent
    data object TogglePasswordVisibility : AuthIntent

    /** Кнопка "Увійти" / "Зареєструватися" або дія клавіатури на останньому полі. */
    data object Submit : AuthIntent
}

sealed interface AuthEffect {
    data class FocusField(val field: AuthField) : AuthEffect
    data class ShowError(val error: AuthError) : AuthEffect
    data class Registered(val username: String) : AuthEffect
    data object NavigateToHome : AuthEffect
}
