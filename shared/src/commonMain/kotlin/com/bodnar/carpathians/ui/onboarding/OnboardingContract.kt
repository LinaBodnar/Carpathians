package com.bodnar.carpathians.ui.onboarding

data class OnboardingState(
    val pageCount: Int,
    val currentPage: Int = 0,
) {
    val isLastPage: Boolean get() = currentPage == pageCount - 1
}

sealed interface OnboardingIntent {
    data class PageChanged(val page: Int) : OnboardingIntent
    data object NextClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
    data object FinishClicked : OnboardingIntent
}

sealed interface OnboardingEffect {
    data class ScrollToPage(val page: Int) : OnboardingEffect
    data object NavigateToAuth : OnboardingEffect
}
