package com.bodnar.carpathians.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

/**
 * Дизайн Carpathians — лише темний ("нічні гори"), тому тема має одну
 * темну колірну схему.
 */
private val CarpathiansColorScheme = darkColorScheme(
    primary = CarpathiansColors.Primary,
    onPrimary = CarpathiansColors.White,
    primaryContainer = CarpathiansColors.PrimaryDark,
    onPrimaryContainer = CarpathiansColors.White,
    secondary = CarpathiansColors.PrimaryDark,
    onSecondary = CarpathiansColors.White,
    tertiary = CarpathiansColors.Warning,
    background = CarpathiansColors.BackgroundBottom,
    onBackground = CarpathiansColors.TextMain,
    surface = CarpathiansColors.BackgroundTop,
    onSurface = CarpathiansColors.TextMain,
    surfaceVariant = CarpathiansColors.MenuGlass,
    onSurfaceVariant = CarpathiansColors.TextPleasant,
    outline = CarpathiansColors.GlassBorderStrong,
    outlineVariant = CarpathiansColors.GlassBorder,
    error = CarpathiansColors.Danger,
)

/** Фоновий градієнт сторінки: #1B2E2B -> #050A09. */
val CarpathiansBackgroundBrush: Brush = Brush.verticalGradient(
    listOf(CarpathiansColors.BackgroundTop, CarpathiansColors.BackgroundBottom)
)

/** Градієнт кнопки "Вхід" (.btn-login-custom). */
val CarpathiansButtonBrush: Brush = Brush.horizontalGradient(
    listOf(CarpathiansColors.PrimaryDark, CarpathiansColors.PrimaryDeep)
)

@Composable
fun CarpathiansTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CarpathiansColorScheme,
        typography = carpathiansTypography(),
        shapes = carpathiansMaterialShapes,
        content = content,
    )
}

/** Контейнер з фоновим градієнтом теми. */
@Composable
fun CarpathiansBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CarpathiansBackgroundBrush),
        content = content,
    )
}
