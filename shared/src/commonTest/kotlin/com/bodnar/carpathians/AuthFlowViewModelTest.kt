package com.bodnar.carpathians

import com.bodnar.carpathians.data.onboarding.OnboardingRepositoryImpl
import com.bodnar.carpathians.domain.auth.AuthError
import com.bodnar.carpathians.domain.auth.AuthResult
import com.bodnar.carpathians.domain.auth.GetCurrentUserUseCase
import com.bodnar.carpathians.domain.auth.IsUserLoggedInUseCase
import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.LogoutUseCase
import com.bodnar.carpathians.domain.auth.ObserveSessionExpiredUseCase
import com.bodnar.carpathians.domain.onboarding.IsOnboardingCompletedUseCase
import com.bodnar.carpathians.navigation.Route
import com.bodnar.carpathians.settings.AppSettings
import com.bodnar.carpathians.ui.auth.AuthEffect
import com.bodnar.carpathians.ui.auth.AuthIntent
import com.bodnar.carpathians.ui.auth.AuthViewModel
import com.bodnar.carpathians.ui.auth.AuthField
import com.bodnar.carpathians.ui.auth.AuthMode
import com.bodnar.carpathians.ui.auth.AuthValidators
import com.bodnar.carpathians.domain.auth.RegisterUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateNameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePhoneUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePeopleCountUseCase
import com.bodnar.carpathians.data.routes.RoutesRepositoryImpl
import com.bodnar.carpathians.domain.routes.Difficulty
import com.bodnar.carpathians.domain.routes.GetRouteDetailsUseCase
import com.bodnar.carpathians.domain.routes.GetRoutesUseCase
import com.bodnar.carpathians.ui.routes.BookingField
import com.bodnar.carpathians.ui.routes.RouteDetailsEffect
import com.bodnar.carpathians.ui.routes.RouteDetailsIntent
import com.bodnar.carpathians.ui.routes.RouteDetailsViewModel
import com.bodnar.carpathians.ui.routes.RoutesEffect
import com.bodnar.carpathians.ui.routes.RoutesIntent
import com.bodnar.carpathians.ui.routes.RoutesViewModel
import kotlin.test.assertNull
import kotlin.test.assertTrue
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
        AuthValidators(),
        LoginUseCase(repository),
        RegisterUseCase(repository),
    )

    private fun AuthViewModel.enter(username: String, password: String) {
        onIntent(AuthIntent.FieldChanged(AuthField.Username, username))
        onIntent(AuthIntent.FieldChanged(AuthField.Password, password))
        onIntent(AuthIntent.Submit)
    }

    @Test
    fun successfulRegistrationReturnsToSignInWithUsername() = runTest {
        val viewModel = authViewModel()
        viewModel.onIntent(AuthIntent.ModeChanged(AuthMode.SignUp))
        mapOf(
            AuthField.FirstName to "Ліна",
            AuthField.Username to "lina",
            AuthField.Email to "lina@mail.com",
            AuthField.Password to "secret1",
            AuthField.ConfirmPassword to "secret1",
        ).forEach { (field, value) -> viewModel.onIntent(AuthIntent.FieldChanged(field, value)) }
        viewModel.onIntent(AuthIntent.Submit)

        assertEquals(AuthEffect.Registered("lina"), viewModel.effects.first())
        assertEquals("lina@mail.com", repository.lastRegistration?.email)
        val state = viewModel.state.value
        assertEquals(AuthMode.SignIn, state.mode)
        assertEquals("lina", state.value(AuthField.Username))
        assertEquals("", state.value(AuthField.Password))
    }

    @Test
    fun routesListIsFilteredByDifficulty() = runTest {
        val viewModel = RoutesViewModel(GetRoutesUseCase(RoutesRepositoryImpl()))
        assertEquals(8, viewModel.state.value.routes.size)

        viewModel.onIntent(RoutesIntent.FilterSelected(Difficulty.Easy))
        assertEquals(listOf("kukul"), viewModel.state.value.routes.map { it.id })

        viewModel.onIntent(RoutesIntent.RouteClicked("kukul"))
        assertEquals(RoutesEffect.OpenRoute("kukul"), viewModel.effects.first())
    }

    private fun routeDetails(id: String) = RouteDetailsViewModel(
        id,
        GetRouteDetailsUseCase(RoutesRepositoryImpl()),
        ValidateNameUseCase(),
        ValidatePhoneUseCase(),
        ValidatePeopleCountUseCase(),
    )

    @Test
    fun routeDetailsLoadsRouteAndOpensMap() = runTest {
        val viewModel = routeDetails("hoverla")
        val route = viewModel.state.value.route!!
        assertEquals(2061, route.maxAltitude)

        viewModel.onIntent(RouteDetailsIntent.OpenMap)
        assertEquals(RouteDetailsEffect.OpenUrl(route.mapUrl), viewModel.effects.first())

        assertTrue(routeDetails("unknown").state.value.isNotFound)
    }

    @Test
    fun bookingIsValidatedAndAccepted() = runTest {
        val viewModel = routeDetails("kukul")
        viewModel.onIntent(RouteDetailsIntent.BookClicked)
        viewModel.onIntent(RouteDetailsIntent.SubmitBooking)
        assertEquals(RouteDetailsEffect.FocusBookingField(BookingField.Name), viewModel.effects.first())

        viewModel.onIntent(RouteDetailsIntent.BookingFieldChanged(BookingField.Name, "Ліна"))
        viewModel.onIntent(RouteDetailsIntent.BookingFieldChanged(BookingField.Phone, "+380671234567"))
        viewModel.onIntent(RouteDetailsIntent.BookingFieldChanged(BookingField.People, "3"))
        viewModel.onIntent(RouteDetailsIntent.SubmitBooking)

        assertEquals(RouteDetailsEffect.BookingAccepted, viewModel.effects.first())
        assertNull(viewModel.state.value.booking)
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
