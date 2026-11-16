package com.bodnar.carpathians

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.bodnar.carpathians.locale.AppEnvironment
import com.bodnar.carpathians.navigation.AppNavDisplay
import com.bodnar.carpathians.navigation.navSavedStateConfiguration
import com.bodnar.carpathians.navigation.rememberAppViewModelDecorator
import com.bodnar.carpathians.theme.CarpathiansBackground
import com.bodnar.carpathians.theme.CarpathiansTheme
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.navigation.Route
import com.bodnar.carpathians.navigation.replaceAll
import com.bodnar.carpathians.navigation.requiresAuth
import com.bodnar.carpathians.ui.main.MainEffect
import com.bodnar.carpathians.ui.main.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel()) {
    val state by mainViewModel.state.collectAsStateWithLifecycle()

    CarpathiansTheme {
        CarpathiansBackground {
            val startRoute = state.startRoute ?: return@CarpathiansBackground
            val backStack: NavBackStack<NavKey> = rememberNavBackStack(navSavedStateConfiguration, startRoute)
            val viewModelDecorator = rememberAppViewModelDecorator()

            CollectEffects(mainViewModel.effects) { effect ->
                when (effect) {
                    MainEffect.NavigateToAuth -> if ((backStack.lastOrNull() as? Route)?.requiresAuth == true) {
                        backStack.replaceAll(Route.Auth)
                    }
                }
            }

            AppEnvironment {
                AppNavDisplay(backStack, viewModelDecorator)
            }
        }
    }
}
