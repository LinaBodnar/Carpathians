package com.bodnar.carpathians.ui.onboarding

import com.bodnar.carpathians.core.mvi.MviViewModel
import com.bodnar.carpathians.domain.onboarding.CompleteOnboardingUseCase
import com.bodnar.carpathians.domain.onboarding.GetOnboardingPageUseCase
import com.bodnar.carpathians.domain.onboarding.SaveOnboardingPageUseCase
import com.bodnar.carpathians.logging.AppLogger

class OnboardingViewModel(
    getOnboardingPage: GetOnboardingPageUseCase,
    private val saveOnboardingPage: SaveOnboardingPageUseCase,
    private val completeOnboarding: CompleteOnboardingUseCase,
    pageCount: Int = onboardingPages.size,
) : MviViewModel<OnboardingState, OnboardingIntent, OnboardingEffect>(
    OnboardingState(
        pageCount = pageCount,
        currentPage = getOnboardingPage().coerceIn(0, pageCount - 1),
    )
) {
    private val log = AppLogger.withTag("OnboardingVM")

    override fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged -> {
                if (intent.page == currentState.currentPage) return
                log.d { "Page selected: ${intent.page + 1}/${currentState.pageCount}" }
                setState { copy(currentPage = intent.page) }
                saveOnboardingPage(intent.page)
            }

            OnboardingIntent.NextClicked -> {
                if (!currentState.isLastPage) {
                    sendEffect(OnboardingEffect.ScrollToPage(currentState.currentPage + 1))
                } else {
                    finish("completed")
                }
            }

            OnboardingIntent.SkipClicked -> finish("skipped on page ${currentState.currentPage + 1}")
            OnboardingIntent.FinishClicked -> finish("completed")
        }
    }

    private fun finish(reason: String) {
        log.i { "Onboarding $reason" }
        completeOnboarding()
        sendEffect(OnboardingEffect.NavigateToAuth)
    }
}
