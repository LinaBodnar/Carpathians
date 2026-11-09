package com.bodnar.carpathians.data.onboarding

import com.bodnar.carpathians.domain.onboarding.OnboardingRepository
import com.bodnar.carpathians.settings.AppSettings

/** Реалізація репозиторію на основі multiplatform-settings ([AppSettings]). */
class OnboardingRepositoryImpl(
    private val settings: AppSettings,
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Boolean = settings.isOnboardingCompleted

    override fun setOnboardingCompleted(completed: Boolean) {
        settings.isOnboardingCompleted = completed
    }

    override fun getLastPage(): Int = settings.onboardingPage

    override fun saveLastPage(page: Int) {
        settings.onboardingPage = page
    }
}
