package com.bodnar.carpathians

import com.bodnar.carpathians.di.appModules
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.auth.AuthViewModel
import com.bodnar.carpathians.ui.main.MainViewModel
import com.bodnar.carpathians.ui.onboarding.OnboardingViewModel
import com.russhwolf.settings.MapSettings
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

/** Перевіряє, що граф залежностей Koin складається без помилок. */
class KoinModulesTest {

    @Test
    fun allViewModelsCanBeCreated() {
        val testOverrides = module {
            single { AppSettings(MapSettings()) }
        }
        val koin = koinApplication {
            allowOverride(true)
            modules(appModules + testOverrides)
        }.koin

        assertNotNull(koin.get<MainViewModel>())
        assertNotNull(koin.get<OnboardingViewModel>())
        assertNotNull(koin.get<AuthViewModel>())
        koin.close()
    }
}
