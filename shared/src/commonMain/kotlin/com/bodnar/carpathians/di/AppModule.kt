package com.bodnar.carpathians.di

import com.bodnar.carpathians.data.onboarding.OnboardingRepositoryImpl
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.domain.onboarding.CompleteOnboardingUseCase
import com.bodnar.carpathians.domain.onboarding.GetOnboardingPageUseCase
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.domain.onboarding.OnboardingRepository
import com.bodnar.carpathians.domain.onboarding.SaveOnboardingPageUseCase
import com.bodnar.carpathians.locale.customAppLocale
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.auth.AuthViewModel
import com.bodnar.carpathians.ui.main.MainViewModel
import com.bodnar.carpathians.ui.onboarding.OnboardingViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { AppSettings() }
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
}

val domainModule = module {
    factoryOf(::IsOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::GetOnboardingPageUseCase)
    factoryOf(::SaveOnboardingPageUseCase)
    factoryOf(::ValidateUsernameUseCase)
    factoryOf(::ValidatePasswordUseCase)
}

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModel { OnboardingViewModel(get(), get(), get()) }
    viewModelOf(::AuthViewModel)
}

val appModules = listOf(dataModule, domainModule, presentationModule)

/**
 * Запуск Koin. Викликається один раз з точки входу кожної платформи
 * (Android — Application, Desktop/Web — main).
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    AppLogger.init()
    val koin = startKoin {
        appDeclaration()
        modules(appModules)
    }.koin

    // Застосовуємо збережену мову ще до першого кадру
    val settings = koin.get<AppSettings>()
    settings.registerLaunch()
    customAppLocale = settings.appLanguage
}
