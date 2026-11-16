package com.bodnar.carpathians.ui.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.*
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.ui.adaptive.ScreenLayout
import com.bodnar.carpathians.ui.adaptive.currentScreenLayout
import com.bodnar.carpathians.domain.auth.validation.ValidationError
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.AppLanguageSwitcher
import com.bodnar.carpathians.ui.components.AppSnackbarHost
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.FitText
import com.bodnar.carpathians.ui.components.GlassPanel
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.TextShadow
import com.bodnar.carpathians.ui.components.message
import com.bodnar.carpathians.ui.components.showMessage
import org.jetbrains.compose.resources.StringResource
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
    val focusRequesters = remember { AuthField.entries.associateWith { FocusRequester() } }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is AuthEffect.FocusField -> focusRequesters.getValue(effect.field).requestFocus()
            is AuthEffect.ShowError -> snackbarHostState.showMessage(getString(effect.error.message), isError = true)
            is AuthEffect.Registered -> snackbarHostState.showMessage(
                getString(Res.string.auth_registered, effect.username)
            )
            AuthEffect.NavigateToHome -> onNavigateToHome()
        }
    }

    AuthScreen(
        state = state,
        onIntent = viewModel::onIntent,
        snackbarHostState = snackbarHostState,
        layout = currentScreenLayout(),
        focusRequesters = focusRequesters,
    )
}

@Composable
fun AuthScreen(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    layout: ScreenLayout = ScreenLayout.Compact,
    focusRequesters: Map<AuthField, FocusRequester> = remember { AuthField.entries.associateWith { FocusRequester() } },
) {
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onIntent(AuthIntent.Submit)
    }
    val form: @Composable (Modifier) -> Unit = { formModifier ->
        AuthFormPanel(
            state = state,
            onIntent = onIntent,
            focusRequesters = focusRequesters,
            onSubmit = submit,
            modifier = formModifier,
        )
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CarpathiansColors.OverlayStrong)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    .padding(horizontal = if (layout == ScreenLayout.Compact) 16.dp else 32.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandLogo(Modifier.weight(1f).padding(end = 8.dp))
                AppLanguageSwitcher()
            }

            val contentInsets = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .imePadding()

            when (layout) {
                ScreenLayout.Expanded -> Row(
                    modifier = Modifier.weight(1f).fillMaxWidth().then(contentInsets),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AuthHero(
                        titleSize = 72,
                        showQuote = true,
                        modifier = Modifier.weight(1f).padding(horizontal = 40.dp),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 40.dp, vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        form(Modifier.widthIn(max = 460.dp).fillMaxWidth())
                    }
                }

                ScreenLayout.Medium -> Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .then(contentInsets)
                        .padding(horizontal = 48.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AuthHero(titleSize = 68, showQuote = false)
                    Spacer(Modifier.height(32.dp))
                    form(Modifier.widthIn(max = 500.dp).fillMaxWidth())
                    Spacer(Modifier.height(32.dp))
                    HeroQuote()
                }

                ScreenLayout.Compact -> Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .then(contentInsets)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AuthHero(titleSize = 52, showQuote = false)
                    Spacer(Modifier.height(24.dp))
                    form(Modifier.fillMaxWidth())
                    Spacer(Modifier.height(24.dp))
                    HeroQuote()
                }
            }
        }

        AppSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(16.dp),
        )
    }
}

@Composable
private fun AuthHero(
    titleSize: Int,
    showQuote: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AdventureTitle(fontSize = titleSize)
        FitText(
            text = stringResource(Res.string.hero_subtitle),
            style = MaterialTheme.typography.titleLarge.copy(
                shadow = TextShadow,
                fontSize = if (titleSize >= 64) 26.sp else 22.sp,
            ),
            color = CarpathiansColors.White,
            minFontSize = 14.sp,
        )
        if (showQuote) {
            Spacer(Modifier.height(28.dp))
            HeroQuote()
        }
    }
}

@Composable
private fun HeroQuote() {
    Text(
        text = stringResource(Res.string.hero_quote),
        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
        color = CarpathiansColors.TextPleasant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun AuthFormPanel(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    focusRequesters: Map<AuthField, FocusRequester>,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassPanel(modifier.animateContentSize()) {
        Column(Modifier.fillMaxWidth()) {
            AuthModeTabs(
                selected = state.mode,
                enabled = !state.isLoading,
                onSelect = { onIntent(AuthIntent.ModeChanged(it)) },
            )
            Spacer(Modifier.height(18.dp))
            FitText(
                text = stringResource(
                    if (state.mode == AuthMode.SignIn) Res.string.auth_title else Res.string.auth_sign_up_title
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = CarpathiansColors.White,
                minFontSize = 18.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    if (state.mode == AuthMode.SignIn) Res.string.auth_subtitle else Res.string.auth_sign_up_subtitle
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = CarpathiansColors.TextPleasant,
            )
            Spacer(Modifier.height(16.dp))

            val fields = state.fields
            fields.forEachIndexed { index, field ->
                val isLast = index == fields.lastIndex
                AuthTextField(
                    field = field,
                    value = state.value(field),
                    onValueChange = { onIntent(AuthIntent.FieldChanged(field, it)) },
                    error = state.errors[field],
                    enabled = !state.isLoading,
                    isPasswordVisible = state.isPasswordVisible,
                    onTogglePassword = { onIntent(AuthIntent.TogglePasswordVisibility) },
                    imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequesters.getValue(fields[index + 1]).requestFocus() },
                        onDone = { onSubmit() },
                    ),
                    modifier = Modifier.focusRequester(focusRequesters.getValue(field)),
                )
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(12.dp))

            GradientButton(
                text = stringResource(
                    if (state.mode == AuthMode.SignIn) Res.string.auth_sign_in else Res.string.auth_sign_up
                ),
                onClick = onSubmit,
                large = true,
                loading = state.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(
                    if (state.mode == AuthMode.SignIn) Res.string.auth_demo_hint else Res.string.auth_sign_up_hint
                ),
                style = MaterialTheme.typography.bodySmall,
                color = CarpathiansColors.TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AuthModeTabs(
    selected: AuthMode,
    enabled: Boolean,
    onSelect: (AuthMode) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(CarpathiansShapes.Pill)
            .background(CarpathiansColors.Overlay)
            .border(1.dp, CarpathiansColors.GlassBorder, CarpathiansShapes.Pill)
            .padding(4.dp),
    ) {
        AuthMode.entries.forEach { mode ->
            val isSelected = mode == selected
            val background by animateColorAsState(if (isSelected) CarpathiansColors.PrimaryDark else Color.Transparent)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CarpathiansShapes.Pill)
                    .background(background)
                    .clickable(enabled = enabled && !isSelected, role = Role.Tab) { onSelect(mode) }
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                FitText(
                    text = stringResource(if (mode == AuthMode.SignIn) Res.string.auth_tab_sign_in else Res.string.auth_tab_sign_up),
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) CarpathiansColors.White else CarpathiansColors.TextNav,
                    minFontSize = 11.sp,
                )
            }
        }
    }
}

private val AuthField.label: StringResource
    get() = when (this) {
        AuthField.FirstName -> Res.string.auth_first_name
        AuthField.Username -> Res.string.auth_username
        AuthField.Email -> Res.string.auth_email
        AuthField.Password -> Res.string.auth_password
        AuthField.ConfirmPassword -> Res.string.auth_confirm_password
    }

@Composable
private fun AuthTextField(
    field: AuthField,
    value: String,
    onValueChange: (String) -> Unit,
    error: ValidationError?,
    enabled: Boolean,
    isPasswordVisible: Boolean,
    onTogglePassword: () -> Unit,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
) {
    val isPassword = field == AuthField.Password || field == AuthField.ConfirmPassword
    val keyboardType = when (field) {
        AuthField.FirstName -> KeyboardType.Text
        AuthField.Username -> KeyboardType.Ascii
        AuthField.Email -> KeyboardType.Email
        AuthField.Password, AuthField.ConfirmPassword -> KeyboardType.Password
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(field.label), maxLines = 1) },
        isError = error != null,
        supportingText = error?.let { { Text(validationMessage(it)) } },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            autoCorrectEnabled = false,
            capitalization = if (field == AuthField.FirstName) KeyboardCapitalization.Words else KeyboardCapitalization.None,
        ),
        keyboardActions = keyboardActions,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (field == AuthField.Password) {
            {
                TextButton(onClick = onTogglePassword) {
                    Text(
                        text = stringResource(if (isPasswordVisible) Res.string.auth_hide_password else Res.string.auth_show_password),
                        style = MaterialTheme.typography.labelMedium,
                        color = CarpathiansColors.Primary,
                        maxLines = 1,
                    )
                }
            }
        } else null,
        shape = CarpathiansShapes.Chip,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = CarpathiansColors.White,
            unfocusedTextColor = CarpathiansColors.White,
            disabledTextColor = CarpathiansColors.TextMuted,
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
    ValidationError.InvalidName -> stringResource(Res.string.auth_error_invalid_name)
    ValidationError.InvalidEmail -> stringResource(Res.string.auth_error_invalid_email)
    ValidationError.PasswordsDoNotMatch -> stringResource(Res.string.auth_error_passwords_do_not_match)
    ValidationError.InvalidPhone -> stringResource(Res.string.booking_error_phone)
    is ValidationError.OutOfRange -> stringResource(Res.string.booking_error_people, error.min, error.max)
}
