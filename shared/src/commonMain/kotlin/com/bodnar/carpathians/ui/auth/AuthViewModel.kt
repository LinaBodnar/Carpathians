package com.bodnar.carpathians.ui.auth

import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.logging.AppLogger

class AuthViewModel(
    private val validateUsername: ValidateUsernameUseCase,
    private val validatePassword: ValidatePasswordUseCase,
) : MviViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    private val log = AppLogger.withTag("AuthVM")

    override fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UsernameChanged -> setState {
                copy(
                    username = intent.value,
                    usernameError = if (showValidation) validateUsername(intent.value) else null,
                )
            }

            is AuthIntent.PasswordChanged -> setState {
                copy(
                    password = intent.value,
                    passwordError = if (showValidation) validatePassword(intent.value) else null,
                )
            }

            AuthIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            AuthIntent.SignIn -> signIn()
        }
    }

    private fun signIn() {
        if (currentState.isLoading) return

        val usernameError = validateUsername(currentState.username)
        val passwordError = validatePassword(currentState.password)
        setState {
            copy(usernameError = usernameError, passwordError = passwordError, showValidation = true)
        }

        when {
            usernameError != null -> sendEffect(AuthEffect.FocusField(AuthField.Username))
            passwordError != null -> sendEffect(AuthEffect.FocusField(AuthField.Password))
            else -> {
                log.i { "Credentials are valid for '${currentState.username.trim()}'" }
                sendEffect(AuthEffect.CredentialsAccepted)
            }
        }
    }
}
