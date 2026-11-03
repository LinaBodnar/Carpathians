package com.bodnar.carpathians

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.theme.CarpathiansBackground
import com.bodnar.carpathians.theme.CarpathiansTheme
import com.bodnar.carpathians.ui.home.HomeScreen
import com.bodnar.carpathians.ui.onboarding.OnboardingScreen

private enum class Screen { Onboarding, Home }

@Composable
@Preview
fun App() {
    val settings = remember {
        AppLogger.init()
        AppSettings().also { it.registerLaunch() }
    }
    val log = remember { AppLogger.withTag("App") }

    var screen by remember {
        mutableStateOf(if (settings.isOnboardingCompleted) Screen.Home else Screen.Onboarding)
    }

    CarpathiansTheme {
        CarpathiansBackground {
            Crossfade(targetState = screen) { current ->
                when (current) {
                    Screen.Onboarding -> OnboardingScreen(
                        initialPage = settings.onboardingPage,
                        onPageChanged = { settings.onboardingPage = it },
                        onFinished = {
                            settings.isOnboardingCompleted = true
                            settings.onboardingPage = 0
                            log.i { "Navigate: Onboarding -> Home" }
                            screen = Screen.Home
                        },
                    )

                    Screen.Home -> HomeScreen(
                        onShowOnboarding = {
                            settings.isOnboardingCompleted = false
                            log.i { "Navigate: Home -> Onboarding" }
                            screen = Screen.Onboarding
                        },
                    )
                }
            }
        }
    }
}
