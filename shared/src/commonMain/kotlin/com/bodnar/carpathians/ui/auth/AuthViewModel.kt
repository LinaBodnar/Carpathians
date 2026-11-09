package com.bodnar.carpathians.ui.auth

import androidx.lifecycle.viewModelScope
import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.RegisterUseCase
import com.bodnar.carpathians.domain.auth.SignUpData
import com.bodnar.carpathians.domain.auth.validation.ValidateEmailUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateNameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordConfirmationUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.logging.AppLogger
import kotlinx.coroutines.launch

/** Набір валідаторів форми (щоб не тягнути п'ять параметрів у конструктор). */
class AuthValidators(
    val username: ValidateUsernameUseCase = ValidateUsernameUseCase(),
    val password: ValidatePasswordUseCase = ValidatePasswordUseCase(),
    val name: ValidateNameUseCase = ValidateNameUseCase(),
    val email: ValidateEmailUseCase = ValidateEmailUseCase(),
    val confirmation: ValidatePasswordConfirmationUseCase = ValidatePasswordConfirmationUseCase(),
)

class AuthViewModel(
    private val validators: AuthValidators,
    private val login: LoginUseCase,
    private val register: RegisterUseCase,
) : MviViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    private val log = AppLogger.withTag("AuthVM")

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.ModeChanged -> if (intent.mode != currentState.mode && !currentState.isLoading) {
                setState { copy(mode = intent.mode, errors = emptyMap(), showValidation = false) }
            }

            is AuthIntent.FieldChanged -> setState {
                val newValues = values + (intent.field to intent.value)
                copy(
                    values = newValues,
                    errors = if (showValidation) validate(copy(values = newValues)) else emptyMap(),
                )
            }

            AuthIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            AuthIntent.Submit -> submit()
        }
    }

    /** Перевіряє лише поля поточної вкладки. */
    private fun validate(state: AuthState): Map<AuthField, ValidationError> = buildMap {
        state.fields.forEach { field ->
            val value = state.value(field)
            val error = when (field) {
                AuthField.FirstName -> validators.name(value)
                AuthField.Username -> validators.username(value)
                AuthField.Email -> validators.email(value)
                AuthField.Password -> validators.password(value)
                AuthField.ConfirmPassword -> validators.confirmation(state.value(AuthField.Password), value)
            }
            if (error != null) put(field, error)
        }
    }

    private fun submit() {
        if (currentState.isLoading) return

        val errors = validate(currentState)
        setState { copy(errors = errors, showValidation = true) }

        val firstInvalid = currentState.fields.firstOrNull { it in errors }
        if (firstInvalid != null) {
            sendEffect(AuthEffect.FocusField(firstInvalid))
            return
        }
        when (currentState.mode) {
            AuthMode.SignIn -> performLogin()
            AuthMode.SignUp -> performRegistration()
        }
    }

    private fun performLogin() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val result = login(currentState.value(AuthField.Username), currentState.value(AuthField.Password))
            setState { copy(isLoading = false) }
            when (result) {
                is AuthResult.Success -> {
                    log.i { "Signed in as ${result.data.username}" }
                    setState { copy(values = values - AuthField.Password) }
                    sendEffect(AuthEffect.NavigateToHome)
                }

                is AuthResult.Failure -> {
                    log.w { "Sign in failed: ${result.error}" }
                    sendEffect(AuthEffect.ShowError(result.error))
                }
            }
        }
    }

    private fun performRegistration() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val state = currentState
            val result = register(
                SignUpData(
                    firstName = state.value(AuthField.FirstName),
                    username = state.value(AuthField.Username),
                    email = state.value(AuthField.Email),
                    password = state.value(AuthField.Password),
                )
            )
            setState { copy(isLoading = false) }
            when (result) {
                is AuthResult.Success -> {
                    log.i { "Registered ${result.data.username}" }
                    // Повертаємося на вкладку входу з уже введеним іменем користувача
                    setState {
                        copy(
                            mode = AuthMode.SignIn,
                            values = mapOf(AuthField.Username to result.data.username),
                            errors = emptyMap(),
                            showValidation = false,
                        )
                    }
                    sendEffect(AuthEffect.Registered(result.data.username))
                }

                is AuthResult.Failure -> {
                    log.w { "Registration failed: ${result.error}" }
                    sendEffect(AuthEffect.ShowError(result.error))
                }
            }
        }
    }
}
