package com.bodnar.carpathians.ui.auth

import androidx.lifecycle.viewModelScope
import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.logging.AppLogger
import kotlinx.coroutines.launch

class AuthViewModel(
    private val validateUsername: ValidateUsernameUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val login: LoginUseCase,
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
            else -> performLogin()
        }
    }

    private fun performLogin() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            val result = login(currentState.username, currentState.password)
            setState { copy(isLoading = false) }
            when (result) {
                is AuthResult.Success -> {
                    log.i { "Signed in as ${result.data.username}" }
                    setState { copy(password = "") }
                    sendEffect(AuthEffect.NavigateToHome)
                }

                is AuthResult.Failure -> {
                    log.w { "Sign in failed: ${result.error}" }
                    sendEffect(AuthEffect.ShowError(result.error))
                }
            }
        }
    }
}
