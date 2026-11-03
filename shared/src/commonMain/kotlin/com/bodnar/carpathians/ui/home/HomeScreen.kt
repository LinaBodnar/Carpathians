package com.bodnar.carpathians.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.hero_subtitle
import carpathians.shared.generated.resources.home_choose_route
import carpathians.shared.generated.resources.home_quote
import carpathians.shared.generated.resources.home_routes_soon
import carpathians.shared.generated.resources.home_show_onboarding
import carpathians.shared.generated.resources.home_sign_in
import carpathians.shared.generated.resources.onboarding_welcome
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.ui.components.AdventureTitle
import com.bodnar.carpathians.ui.components.BrandLogo
import com.bodnar.carpathians.ui.components.GradientButton
import com.bodnar.carpathians.ui.components.RouteButton
import com.bodnar.carpathians.ui.components.TextShadow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val log = AppLogger.withTag("Home")

/** Головний екран — hero-секція дизайну (ADVENTURE + "Обрати маршрут"). */
@Composable
fun HomeScreen(
    onShowOnboarding: () -> Unit,
    modifier: Modifier = Modifier,
    headerActions: @Composable RowScope.() -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                    0f to Color.Black.copy(alpha = 0.35f),
                    0.6f to Color.Transparent,
                    1f to CarpathiansColors.BackgroundBottom.copy(alpha = 0.9f),
                )
            )
        )

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
            headerActions()
            GradientButton(
                text = stringResource(Res.string.home_sign_in),
                onClick = { log.d { "Sign in clicked" } },
            )
        }

        // Hero-текст по центру
        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AdventureTitle(fontSize = 60)
            Text(
                text = stringResource(Res.string.hero_subtitle),
                style = MaterialTheme.typography.headlineSmall.copy(shadow = TextShadow),
                color = CarpathiansColors.White,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val soonMessage = Res.string.home_routes_soon
            RouteButton(
                text = stringResource(Res.string.home_choose_route),
                onClick = {
                    log.d { "Choose route clicked" }
                    scope.launch { snackbarHostState.showSnackbar(getString(soonMessage)) }
                },
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.home_quote),
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = CarpathiansColors.TextPleasant,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = {
                log.i { "Show onboarding again" }
                onShowOnboarding()
            }) {
                Text(
                    text = stringResource(Res.string.home_show_onboarding),
                    style = MaterialTheme.typography.titleSmall,
                    color = CarpathiansColors.Primary,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 140.dp),
        )
    }
}
