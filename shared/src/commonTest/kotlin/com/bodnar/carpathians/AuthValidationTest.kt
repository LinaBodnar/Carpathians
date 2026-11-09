package com.bodnar.carpathians

import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.ui.auth.AuthEffect
import com.bodnar.carpathians.ui.auth.AuthField
import com.bodnar.carpathians.ui.auth.AuthIntent
import com.bodnar.carpathians.ui.auth.AuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthValidationTest {

    private val validateUsername = ValidateUsernameUseCase()
    private val validatePassword = ValidatePasswordUseCase()

    @Test
    fun usernameValidation() {
        assertEquals(ValidationError.Empty, validateUsername("   "))
        assertEquals(ValidationError.TooShort(3), validateUsername("em"))
        assertEquals(ValidationError.InvalidCharacters, validateUsername("емілі"))
        assertNull(validateUsername("emilys"))
        assertNull(validateUsername(" emily.s_1 "))
    }

    @Test
    fun passwordValidation() {
        assertEquals(ValidationError.Empty, validatePassword(""))
        assertEquals(ValidationError.ContainsSpaces, validatePassword("emily pass"))
        assertEquals(ValidationError.TooShort(6), validatePassword("12345"))
        assertNull(validatePassword("emilyspass"))
    }

    private fun createViewModel() = AuthViewModel(validateUsername, validatePassword, LoginUseCase(FakeAuthRepository()))

    @Test
    fun errorsAreHiddenUntilFirstSignIn() {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.UsernameChanged("a"))
        assertNull(viewModel.state.value.usernameError)
    }

    @Test
    fun signInWithEmptyFieldsShowsErrorsAndFocusesUsername() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.SignIn)

        val state = viewModel.state.value
        assertEquals(ValidationError.Empty, state.usernameError)
        assertEquals(ValidationError.Empty, state.passwordError)
        assertEquals(AuthEffect.FocusField(AuthField.Username), viewModel.effects.first())

        // Після першої спроби помилки оновлюються під час введення
        viewModel.onIntent(AuthIntent.UsernameChanged("emilys"))
        assertNull(viewModel.state.value.usernameError)
    }

    @Test
    fun invalidPasswordFocusesPasswordField() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.UsernameChanged("emilys"))
        viewModel.onIntent(AuthIntent.PasswordChanged("123"))
        viewModel.onIntent(AuthIntent.SignIn)

        assertEquals(AuthEffect.FocusField(AuthField.Password), viewModel.effects.first())
    }

    @Test
    fun passwordVisibilityToggles() {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.TogglePasswordVisibility)
        assertTrue(viewModel.state.value.isPasswordVisible)
    }
}
