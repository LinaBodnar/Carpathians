package com.bodnar.carpathians

import com.bodnar.carpathians.di.appModules
import com.bodnar.carpathians.domain.auth.TokenStorage
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.auth.AuthViewModel
import com.bodnar.carpathians.ui.home.HomeViewModel
import com.bodnar.carpathians.ui.main.MainViewModel
import com.bodnar.carpathians.ui.onboarding.OnboardingViewModel
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

/** Перевіряє, що граф залежностей Koin складається без помилок. */
@OptIn(ExperimentalCoroutinesApi::class)
class KoinModulesTest {

    @Test
    fun allViewModelsCanBeCreated() {
        Dispatchers.setMain(StandardTestDispatcher())
        // Реальні сховища замінено на тестові, щоб не писати на диск
        val testOverrides = module {
            single { AppSettings(MapSettings()) }
            single<TokenStorage> { InMemoryTokenStorage() }
        }
        val koin = koinApplication {
            allowOverride(true)
            modules(appModules + testOverrides)
        }.koin

        try {
            assertNotNull(koin.get<MainViewModel>())
            assertNotNull(koin.get<OnboardingViewModel>())
            assertNotNull(koin.get<AuthViewModel>())
            assertNotNull(koin.get<HomeViewModel>())
        } finally {
            koin.close()
            Dispatchers.resetMain()
        }
    }
}
