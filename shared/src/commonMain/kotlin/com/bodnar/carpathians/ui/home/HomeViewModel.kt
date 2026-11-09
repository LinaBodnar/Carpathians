package com.bodnar.carpathians.ui.home

import androidx.lifecycle.viewModelScope
import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.GetCurrentUserUseCase
import com.bodnar.carpathians.domain.auth.LogoutUseCase
import com.bodnar.carpathians.logging.AppLogger
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val logout: LogoutUseCase,
) : MviViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    private val log = AppLogger.withTag("HomeVM")

    init {
        loadUser()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.Refresh -> loadUser()
            HomeIntent.Logout -> viewModelScope.launch {
                logout()
                sendEffect(HomeEffect.NavigateToAuth)
            }
        }
    }

    /** Захищений запит /auth/me: якщо access-токен прострочений, Ktor сам оновить його. */
    private fun loadUser() {
        if (currentState.isLoading) return
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = getCurrentUser()) {
                is AuthResult.Success -> {
                    log.i { "Profile loaded: ${result.data.username}" }
                    setState { copy(user = result.data, isLoading = false) }
                }

                is AuthResult.Failure -> {
                    setState { copy(isLoading = false) }
                    sendEffect(HomeEffect.ShowError(result.error))
                    if (result.error == AuthError.SessionExpired) {
                        log.w { "Session expired, back to sign in" }
                        sendEffect(HomeEffect.NavigateToAuth)
                    }
                }
            }
        }
    }
}
