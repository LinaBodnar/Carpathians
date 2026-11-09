package com.bodnar.carpathians

import com.bodnar.carpathians.data.onboarding.OnboardingRepositoryImpl
import com.bodnar.carpathians.domain.onboarding.CompleteOnboardingUseCase
import com.bodnar.carpathians.domain.onboarding.GetOnboardingPageUseCase
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.domain.onboarding.SaveOnboardingPageUseCase
import com.bodnar.carpathians.navigation.Route
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.main.MainViewModel
import com.bodnar.carpathians.ui.onboarding.OnboardingEffect
import com.bodnar.carpathians.ui.onboarding.OnboardingIntent
import com.bodnar.carpathians.ui.onboarding.OnboardingViewModel
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingTest {

    private val settings = AppSettings(MapSettings())
    private val repository = OnboardingRepositoryImpl(settings)

    private fun createViewModel() = OnboardingViewModel(
        getOnboardingPage = GetOnboardingPageUseCase(repository),
        saveOnboardingPage = SaveOnboardingPageUseCase(repository),
        completeOnboarding = CompleteOnboardingUseCase(repository),
        pageCount = 5,
    )

    @Test
    fun completeOnboardingUseCaseSavesResult() {
        repository.saveLastPage(3)
        CompleteOnboardingUseCase(repository)()

        assertTrue(IsOnboardingCompletedUseCase(repository)())
        assertEquals(0, repository.getLastPage())
    }

    @Test
    fun viewModelRestoresSavedPage() {
        repository.saveLastPage(2)
        assertEquals(2, createViewModel().state.value.currentPage)
    }

    @Test
    fun pageChangeIsSaved() {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.PageChanged(4))

        assertEquals(4, viewModel.state.value.currentPage)
        assertTrue(viewModel.state.value.isLastPage)
        assertEquals(4, repository.getLastPage())
    }

    @Test
    fun nextScrollsToFollowingPage() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.NextClicked)

        assertEquals(OnboardingEffect.ScrollToPage(1), viewModel.effects.first())
    }

    @Test
    fun skipCompletesOnboardingAndNavigatesToAuth() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.SkipClicked)

        assertEquals(OnboardingEffect.NavigateToAuth, viewModel.effects.first())
        assertTrue(settings.isOnboardingCompleted)
    }

    @Test
    fun mainViewModelChoosesStartScreen() {
        val isCompleted = IsOnboardingCompletedUseCase(repository)
        assertFalse(settings.isOnboardingCompleted)
        assertEquals(Route.Onboarding, MainViewModel(isCompleted).state.value.startRoute)

        CompleteOnboardingUseCase(repository)()
        assertEquals(Route.Auth, MainViewModel(isCompleted).state.value.startRoute)
    }
}
