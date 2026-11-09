package com.bodnar.carpathians.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.home_email
import carpathians.shared.generated.resources.home_greeting
import carpathians.shared.generated.resources.home_logout
import carpathians.shared.generated.resources.home_refresh
import carpathians.shared.generated.resources.home_subtitle
import carpathians.shared.generated.resources.home_username
import carpathians.shared.generated.resources.onboarding_routes
import com.bodnar.carpathians.core.mvi.CollectEffects
import com.bodnar.carpathians.domain.auth.User
import com.bodnar.carpathians.theme.CarpathiansButtonBrush
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.AppLanguageSwitcher
import com.bodnar.carpathians.ui.components.AppSnackbarHost
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.GlassPanel
import com.bodnar.carpathians.ui.components.RouteButton
import com.bodnar.carpathians.ui.components.TextShadow
import com.bodnar.carpathians.ui.components.message
import com.bodnar.carpathians.ui.components.FitText
import com.bodnar.carpathians.ui.components.showMessage
import androidx.compose.ui.text.font.FontStyle
import carpathians.shared.generated.resources.home_choose_route
import carpathians.shared.generated.resources.hero_subtitle
import carpathians.shared.generated.resources.hero_quote
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onNavigateToAuth: () -> Unit,
    onNavigateToRoutes: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is HomeEffect.ShowError -> snackbarHostState.showMessage(getString(effect.error.message), isError = true)
            HomeEffect.NavigateToAuth -> onNavigateToAuth()
            HomeEffect.NavigateToRoutes -> onNavigateToRoutes()
        }
    }

    HomeScreen(state = state, onIntent = viewModel::onIntent, snackbarHostState = snackbarHostState)
}

/** Домашня сторінка авторизованого користувача. */
@Composable
fun HomeScreen(
    state: HomeState,
    onIntent: (HomeIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize().background(CarpathiansColors.BackgroundBottom)) {
        Image(
            painter = painterResource(Res.drawable.onboarding_routes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.5f),
                    0.5f to Color.Black.copy(alpha = 0.2f),
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
                    .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrandLogo(Modifier.weight(1f))
                AppLanguageSwitcher()
                TextButton(onClick = { onIntent(HomeIntent.Logout) }) {
                    Text(
                        text = stringResource(Res.string.home_logout),
                        style = MaterialTheme.typography.titleSmall,
                        color = CarpathiansColors.TextNav,
                        maxLines = 1,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AdventureTitle(fontSize = 56)
                FitText(
                    text = stringResource(Res.string.hero_subtitle),
                    style = MaterialTheme.typography.titleLarge.copy(shadow = TextShadow),
                    color = CarpathiansColors.White,
                    minFontSize = 14.sp,
                )
                Spacer(Modifier.height(24.dp))

                GlassPanel(Modifier.widthIn(max = 420.dp).fillMaxWidth()) {
                    val user = state.user
                    if (user == null) {
                        Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            if (state.isLoading) CircularProgressIndicator(color = CarpathiansColors.Primary)
                        }
                    } else {
                        UserCard(user)
                    }
                }

                Spacer(Modifier.height(24.dp))
                // Як у hero-секції дизайну: .route-button "ОБРАТИ МАРШРУТ"
                RouteButton(
                    text = stringResource(Res.string.home_choose_route),
                    onClick = { onIntent(HomeIntent.OpenRoutes) },
                    showArrow = true,
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { onIntent(HomeIntent.Refresh) }, enabled = !state.isLoading) {
                    Text(
                        text = stringResource(Res.string.home_refresh),
                        style = MaterialTheme.typography.titleSmall,
                        color = CarpathiansColors.Primary,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.height(16.dp))
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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
        )
    }
}

@Composable
private fun UserCard(user: User) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(CarpathiansButtonBrush)
                .border(2.dp, CarpathiansColors.GlassBorderStrong, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.initials,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp),
                color = CarpathiansColors.White,
            )
        }
        Spacer(Modifier.height(14.dp))
        FitText(
            text = stringResource(Res.string.home_greeting, user.firstName.ifBlank { user.username }),
            style = MaterialTheme.typography.headlineMedium.copy(shadow = TextShadow),
            color = CarpathiansColors.White,
            textAlign = TextAlign.Center,
            minFontSize = 16.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = CarpathiansColors.TextPleasant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))
        InfoRow(stringResource(Res.string.home_username), user.username)
        if (user.email.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            InfoRow(stringResource(Res.string.home_email), user.email)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = CarpathiansColors.TextMuted, maxLines = 1)
        Spacer(Modifier.width(12.dp))
        FitText(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = CarpathiansColors.White,
            textAlign = TextAlign.End,
            minFontSize = 10.sp,
            modifier = Modifier.weight(1f),
        )
    }
}
