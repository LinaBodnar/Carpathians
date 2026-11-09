package com.bodnar.carpathians

import com.bodnar.carpathians.domain.auth.LoginUseCase
import com.bodnar.carpathians.domain.auth.RegisterUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateEmailUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateNameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordConfirmationUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePasswordUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePeopleCountUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidatePhoneUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidateUsernameUseCase
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.ui.auth.AuthEffect
import com.bodnar.carpathians.ui.auth.AuthField
import com.bodnar.carpathians.ui.auth.AuthIntent
import com.bodnar.carpathians.ui.auth.AuthMode
import com.bodnar.carpathians.ui.auth.AuthValidators
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

    @Test
    fun signUpFieldsValidation() {
        val name = ValidateNameUseCase()
        assertNull(name("Ліна"))
        assertNull(name("Anna-Maria"))
        assertEquals(ValidationError.InvalidName, name("L1na"))

        val email = ValidateEmailUseCase()
        assertNull(email("lina@mail.com"))
        assertEquals(ValidationError.InvalidEmail, email("lina@mail"))

        val confirmation = ValidatePasswordConfirmationUseCase()
        assertNull(confirmation("secret1", "secret1"))
        assertEquals(ValidationError.PasswordsDoNotMatch, confirmation("secret1", "secret2"))
    }

    @Test
    fun bookingFieldsValidation() {
        val phone = ValidatePhoneUseCase()
        assertNull(phone("+380 67 123 45 67"))
        assertNull(phone("0671234567"))
        assertEquals(ValidationError.InvalidPhone, phone("12345"))
        assertEquals(ValidationError.InvalidPhone, phone("067-abc-4567"))

        val people = ValidatePeopleCountUseCase()
        assertNull(people("4"))
        assertEquals(ValidationError.OutOfRange(1, 20), people("0"))
        assertEquals(ValidationError.OutOfRange(1, 20), people("many"))
    }

    private fun createViewModel() = AuthViewModel(
        AuthValidators(),
        LoginUseCase(FakeAuthRepository()),
        RegisterUseCase(FakeAuthRepository()),
    )

    @Test
    fun errorsAreHiddenUntilFirstSubmit() {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Username, "a"))
        assertTrue(viewModel.state.value.errors.isEmpty())
    }

    @Test
    fun submitWithEmptyFieldsShowsErrorsAndFocusesUsername() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.Submit)

        val errors = viewModel.state.value.errors
        assertEquals(ValidationError.Empty, errors[AuthField.Username])
        assertEquals(ValidationError.Empty, errors[AuthField.Password])
        assertEquals(AuthEffect.FocusField(AuthField.Username), viewModel.effects.first())

        // Після першої спроби помилки оновлюються під час введення
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Username, "emilys"))
        assertNull(viewModel.state.value.errors[AuthField.Username])
    }

    @Test
    fun invalidPasswordFocusesPasswordField() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Username, "emilys"))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Password, "123"))
        viewModel.onIntent(AuthIntent.Submit)

        assertEquals(AuthEffect.FocusField(AuthField.Password), viewModel.effects.first())
    }

    @Test
    fun signUpValidatesOnlyItsFields() = runTest {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.ModeChanged(AuthMode.SignUp))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.FirstName, "Ліна"))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Username, "lina"))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Email, "lina@mail"))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.Password, "secret1"))
        viewModel.onIntent(AuthIntent.FieldChanged(AuthField.ConfirmPassword, "secret2"))
        viewModel.onIntent(AuthIntent.Submit)

        val errors = viewModel.state.value.errors
        assertEquals(setOf(AuthField.Email, AuthField.ConfirmPassword), errors.keys)
        assertEquals(AuthEffect.FocusField(AuthField.Email), viewModel.effects.first())
    }

    @Test
    fun passwordVisibilityToggles() {
        val viewModel = createViewModel()
        viewModel.onIntent(AuthIntent.TogglePasswordVisibility)
        assertTrue(viewModel.state.value.isPasswordVisible)
    }
}
