package com.bodnar.carpathians.domain.onboarding

/** Чи пройшов користувач онбординг (для вибору стартового екрана). */
class IsOnboardingCompletedUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Boolean = repository.isOnboardingCompleted()
}

/** Зберігає результат проходження онбордингу. */
class CompleteOnboardingUseCase(private val repository: OnboardingRepository) {
    operator fun invoke() {
        repository.setOnboardingCompleted(true)
        repository.saveLastPage(0)
    }
}

/** Сторінка, на якій користувач зупинився минулого разу. */
class GetOnboardingPageUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(): Int = repository.getLastPage()
}

/** Запам'ятовує поточну сторінку, щоб продовжити з неї після перезапуску. */
class SaveOnboardingPageUseCase(private val repository: OnboardingRepository) {
    operator fun invoke(page: Int) = repository.saveLastPage(page)
}
