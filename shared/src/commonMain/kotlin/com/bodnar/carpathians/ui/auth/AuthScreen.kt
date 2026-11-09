package com.bodnar.carpathians.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.auth_demo_hint
import carpathians.shared.generated.resources.auth_error_contains_spaces
import carpathians.shared.generated.resources.auth_error_empty
import carpathians.shared.generated.resources.auth_error_invalid_characters
import carpathians.shared.generated.resources.auth_error_too_short
import carpathians.shared.generated.resources.auth_hide_password
import carpathians.shared.generated.resources.auth_password
import carpathians.shared.generated.resources.auth_show_password
import carpathians.shared.generated.resources.auth_sign_in
import carpathians.shared.generated.resources.auth_subtitle
import carpathians.shared.generated.resources.auth_title
import carpathians.shared.generated.resources.auth_username
import carpathians.shared.generated.resources.hero_subtitle
import carpathians.shared.generated.resources.hero_quote
import carpathians.shared.generated.resources.onboarding_welcome
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.AppLanguageSwitcher
import com.bodnar.carpathians.ui.components.AppSnackbarHost
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.GlassPanel
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.TextShadow
import com.bodnar.carpathians.ui.components.message
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthRoute(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val usernameFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is AuthEffect.FocusField -> when (effect.field) {
                AuthField.Username -> usernameFocus.requestFocus()
                AuthField.Password -> passwordFocus.requestFocus()
            }

            is AuthEffect.ShowError -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = getString(effect.error.message),
                    withDismissAction = true,
                )
            }

            AuthEffect.NavigateToHome -> onNavigateToHome()
        }
    }

    AuthScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        usernameFocus = usernameFocus,
        passwordFocus = passwordFocus,
    )
}

/** Екран входу: hero-секція дизайну + "скляна" форма (.glass-login-modal). */
@Composable
fun AuthScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    usernameFocus: FocusRequester = remember { FocusRequester() },
    passwordFocus: FocusRequester = remember { FocusRequester() },
) {
    val focusManager = LocalFocusManager.current

    Box(modifier.fillMaxSize().background(CarpathiansColors.BackgroundBottom)) {
        Image(
            painter = painterResource(Res.drawable.onboarding_welcome),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.45f),
                    0.5f to Color.Black.copy(alpha = 0.15f),
                    1f to CarpathiansColors.BackgroundBottom.copy(alpha = 0.95f),
                )
            )
        )

        Column(Modifier.fillMaxSize()) {
            // Header (.main-header): чорний 70% фон
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CarpathiansColors.OverlayStrong)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandLogo()
                Spacer(Modifier.weight(1f))
                AppLanguageSwitcher()
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                    .imePadding()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AdventureTitle(fontSize = 52)
                Text(
                    text = stringResource(Res.string.hero_subtitle),
                    style = MaterialTheme.typography.titleLarge.copy(shadow = TextShadow),
                    color = CarpathiansColors.White,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(28.dp))

                GlassPanel(Modifier.widthIn(max = 420.dp).fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(Res.string.auth_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = CarpathiansColors.White,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(Res.string.auth_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CarpathiansColors.TextPleasant,
                        )
                        Spacer(Modifier.height(20.dp))

                        // Ім'я користувача: дія клавіатури "Далі" переводить фокус на пароль
                        AuthTextField(
                            value = state.username,
                            onValueChange = { onIntent(AuthIntent.UsernameChanged(it)) },
                            label = stringResource(Res.string.auth_username),
                            error = state.usernameError,
                            enabled = !state.isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
                                imeAction = ImeAction.Next,
                                autoCorrectEnabled = false,
                            ),
                            keyboardActions = KeyboardActions(onNext = { passwordFocus.requestFocus() }),
                            modifier = Modifier.focusRequester(usernameFocus),
                        )
                        Spacer(Modifier.height(12.dp))

                        // Пароль: дія клавіатури "Готово" = кнопка "Увійти"
                        AuthTextField(
                            value = state.password,
                            onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
                            label = stringResource(Res.string.auth_password),
                            error = state.passwordError,
                            enabled = !state.isLoading,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                                autoCorrectEnabled = false,
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                                onIntent(AuthIntent.SignIn)
                            }),
                            visualTransformation = if (state.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                TextButton(onClick = { onIntent(AuthIntent.TogglePasswordVisibility) }) {
                                    Text(
                                        text = stringResource(
                                            if (state.isPasswordVisible) Res.string.auth_hide_password
                                            else Res.string.auth_show_password
                                        ),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CarpathiansColors.Primary,
                                    )
                                }
                            },
                            modifier = Modifier.focusRequester(passwordFocus),
                        )
                        Spacer(Modifier.height(24.dp))

                        GradientButton(
                            text = stringResource(Res.string.auth_sign_in),
                            onClick = {
                                focusManager.clearFocus()
                                onIntent(AuthIntent.SignIn)
                            },
                            large = true,
                            loading = state.isLoading,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(Res.string.auth_demo_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = CarpathiansColors.TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.hero_quote),
                    style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    color = CarpathiansColors.TextPleasant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        AppSnackbarHost(
            hostState = snackbarHostState,
            isError = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(16.dp),
        )
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: ValidationError?,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { { Text(validationMessage(it)) } },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = CarpathiansShapes.Chip,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CarpathiansColors.White,
            unfocusedTextColor = CarpathiansColors.White,
            focusedContainerColor = CarpathiansColors.Glass,
            unfocusedContainerColor = CarpathiansColors.Glass,
            disabledContainerColor = CarpathiansColors.Glass,
            errorContainerColor = CarpathiansColors.Glass,
            focusedBorderColor = CarpathiansColors.Primary,
            unfocusedBorderColor = CarpathiansColors.GlassBorderStrong,
            focusedLabelColor = CarpathiansColors.Primary,
            unfocusedLabelColor = CarpathiansColors.TextNav,
            cursorColor = CarpathiansColors.Primary,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun validationMessage(error: ValidationError): String = when (error) {
    ValidationError.Empty -> stringResource(Res.string.auth_error_empty)
    is ValidationError.TooShort -> stringResource(Res.string.auth_error_too_short, error.minLength)
    ValidationError.InvalidCharacters -> stringResource(Res.string.auth_error_invalid_characters)
    ValidationError.ContainsSpaces -> stringResource(Res.string.auth_error_contains_spaces)
}
