package com.bodnar.carpathians

import com.bodnar.carpathians.data.onboarding.OnboardingRepositoryImpl
import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.GetCurrentUserUseCase
import com.bodnar.carpathians.domain.auth.IsUserLoggedInUseCase
import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.LogoutUseCase
import com.bodnar.carpathians.domain.auth.ObserveSessionExpiredUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.navigation.Route
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.auth.AuthEffect
import com.bodnar.carpathians.ui.auth.AuthIntent
import com.bodnar.carpathians.ui.auth.AuthViewModel
import com.bodnar.carpathians.ui.home.HomeEffect
import com.bodnar.carpathians.ui.home.HomeIntent
import com.bodnar.carpathians.ui.home.HomeViewModel
import com.bodnar.carpathians.ui.main.MainEffect
import com.bodnar.carpathians.ui.main.MainViewModel
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class AuthFlowViewModelTest {

    private val repository = FakeAuthRepository()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun authViewModel() = AuthViewModel(
        ValidateUsernameUseCase(),
        ValidatePasswordUseCase(),
        LoginUseCase(repository),
    )

    private fun AuthViewModel.enter(username: String, password: String) {
        onIntent(AuthIntent.UsernameChanged(username))
        onIntent(AuthIntent.PasswordChanged(password))
        onIntent(AuthIntent.SignIn)
    }

    @Test
    fun successfulLoginNavigatesHome() = runTest {
        val viewModel = authViewModel()
        viewModel.enter(" emilys ", "emilyspass")

        assertEquals(AuthEffect.NavigateToHome, viewModel.effects.first())
        assertEquals("emilys" to "emilyspass", repository.lastLogin)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun failedLoginShowsErrorSnackbar() = runTest {
        repository.loginResult = AuthResult.Failure(AuthError.InvalidCredentials)
        val viewModel = authViewModel()
        viewModel.enter("emilys", "wrongpass")

        assertEquals(AuthEffect.ShowError(AuthError.InvalidCredentials), viewModel.effects.first())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun invalidFormDoesNotCallServer() {
        authViewModel().enter("", "")
        assertEquals(null, repository.lastLogin)
    }

    @Test
    fun homeLoadsProfileAndLogsOut() = runTest {
        repository.loggedIn = true
        val viewModel = HomeViewModel(GetCurrentUserUseCase(repository), LogoutUseCase(repository))
        assertEquals(testUser, viewModel.state.value.user)

        viewModel.onIntent(HomeIntent.Logout)
        assertEquals(HomeEffect.NavigateToAuth, viewModel.effects.first())
        assertFalse(repository.loggedIn)
    }

    private fun mainViewModel(onboardingCompleted: Boolean): MainViewModel {
        val onboarding = OnboardingRepositoryImpl(AppSettings(MapSettings()))
        onboarding.setOnboardingCompleted(onboardingCompleted)
        return MainViewModel(
            IsOnboardingCompletedUseCase(onboarding),
            IsUserLoggedInUseCase(repository),
            ObserveSessionExpiredUseCase(repository),
        )
    }

    @Test
    fun startScreenDependsOnOnboardingAndSession() {
        assertEquals(Route.Onboarding, mainViewModel(onboardingCompleted = false).state.value.startRoute)
        assertEquals(Route.Auth, mainViewModel(onboardingCompleted = true).state.value.startRoute)

        repository.loggedIn = true
        assertEquals(Route.Home, mainViewModel(onboardingCompleted = true).state.value.startRoute)
    }

    @Test
    fun expiredSessionNavigatesToAuth() = runTest {
        val viewModel = mainViewModel(onboardingCompleted = true)
        repository.sessionExpired.emit(Unit)
        assertEquals(MainEffect.NavigateToAuth, viewModel.effects.first())
    }
}
