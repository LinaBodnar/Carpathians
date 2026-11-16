package com.bodnar.carpathians.domain.onboarding

class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isOnboardingCompleted()
}

class CompleteOnboardingUseCase(private val repository: OnboardingRepository) {
    operator fun invoke() {
        repository.setOnboardingCompleted(true)
        repository.saveLastPage(0)
    }
}

class GetOnboardingPageUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Int = repository.getLastPage()
}

class SaveOnboardingPageUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(page: Int) = repository.saveLastPage(page)
}
