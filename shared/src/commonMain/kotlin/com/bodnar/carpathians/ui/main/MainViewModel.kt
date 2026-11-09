package com.bodnar.carpathians.ui.main

import androidx.lifecycle.ViewModel
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainState(
    /** Екран, з якого стартує застосунок; null — ще визначається. */
    val startRoute: Route? = null,
)

/**
 * ViewModel головного (кореневого) екрана: визначає, куди вести користувача
 * при запуску — на онбординг чи одразу на екран авторизації.
 */
class MainViewModel(
    private val isOnboardingCompleted: IsOnboardingCompletedUseCase,
) : ViewModel() {

    private val log = AppLogger.withTag("MainVM")

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        val start = if (isOnboardingCompleted()) Route.Auth else Route.Onboarding
        log.i { "Start destination: $start" }
        _state.value = MainState(startRoute = start)
    }
}
