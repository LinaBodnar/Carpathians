package com.bodnar.carpathians.domain.onboarding

/** Доступ до стану проходження онбордингу. */
interface OnboardingRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted(completed: Boolean)

    fun getLastPage(): Int
    fun saveLastPage(page: Int)
}
