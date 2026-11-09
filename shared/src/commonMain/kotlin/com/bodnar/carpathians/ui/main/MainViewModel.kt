package com.bodnar.carpathians.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bodnar.carpathians.domain.auth.IsUserLoggedInUseCase
import com.bodnar.carpathians.domain.auth.ObserveSessionExpiredUseCase
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.navigation.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class MainState(
    /** Екран, з якого стартує застосунок; null — ще визначається. */
    val startRoute: Route? = null,
)

sealed interface MainEffect {
    /** Сесія закінчилась під час роботи — повертаємо користувача на вхід. */
    data object NavigateToAuth : MainEffect
}

/**
 * ViewModel головного (кореневого) екрана: визначає стартовий екран
 *  - онбординг не пройдено → Onboarding;
 *  - користувач авторизований (є токени) → Home;
 *  - інакше → Auth;
 * і стежить за подією завершення сесії.
 */
class MainViewModel(
    private val isOnboardingCompleted: IsOnboardingCompletedUseCase,
    private val isUserLoggedIn: IsUserLoggedInUseCase,
    observeSessionExpired: ObserveSessionExpiredUseCase,
) : ViewModel() {

    private val log = AppLogger.withTag("MainVM")

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _effects = Channel<MainEffect>(Channel.BUFFERED)
    val effects: Flow<MainEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val start = when {
                !isOnboardingCompleted() -> Route.Onboarding
                isUserLoggedIn() -> Route.Home
                else -> Route.Auth
            }
            log.i { "Start destination: $start" }
            _state.value = MainState(startRoute = start)
        }
        viewModelScope.launch {
            observeSessionExpired().collect {
                log.w { "Session expired" }
                _effects.send(MainEffect.NavigateToAuth)
            }
        }
    }
}
