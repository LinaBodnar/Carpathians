package com.bodnar.carpathians

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bodnar.carpathians.locale.AppEnvironment
import com.bodnar.carpathians.locale.AppLanguage
import com.bodnar.carpathians.locale.customAppLocale
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.theme.CarpathiansBackground
import com.bodnar.carpathians.theme.CarpathiansTheme
import com.bodnar.carpathians.ui.components.LanguageSwitcher
import com.bodnar.carpathians.ui.home.HomeScreen
import com.bodnar.carpathians.ui.onboarding.OnboardingScreen

private enum class Screen { Onboarding, Home }

@Composable
@Preview
fun App() {
    val log = remember { AppLogger.withTag("App") }

    // Мова системи запам'ятовується до застосування збереженої мови
    val systemLanguage = remember {
        AppLanguage.fromTag(Locale.current.language) ?: AppLanguage.English
    }
    val settings = remember {
        AppLogger.init()
        AppSettings().also {
            it.registerLaunch()
            customAppLocale = it.appLanguage
            log.i { "System language: ${systemLanguage.tag}, saved language: ${it.appLanguage}" }
        }
    }

    var screen by remember {
        mutableStateOf(if (settings.isOnboardingCompleted) Screen.Home else Screen.Onboarding)
    }
    val currentLanguage = AppLanguage.fromTag(customAppLocale) ?: systemLanguage

    val languageSwitcher: @Composable () -> Unit = {
        LanguageSwitcher(
            selected = currentLanguage,
            onSelect = { language ->
                log.i { "Language changed: ${currentLanguage.tag} -> ${language.tag}" }
                settings.appLanguage = language.tag
                customAppLocale = language.tag
            },
            modifier = Modifier.padding(end = 8.dp),
        )
    }

    AppEnvironment {
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
                            topBarActions = { languageSwitcher() },
                        )

                        Screen.Home -> HomeScreen(
                            onShowOnboarding = {
                                settings.isOnboardingCompleted = false
                                log.i { "Navigate: Home -> Onboarding" }
                                screen = Screen.Onboarding
                            },
                            headerActions = { languageSwitcher() },
                        )
                    }
                }
            }
        }
    }
}
