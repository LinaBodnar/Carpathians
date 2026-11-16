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

val CarpathiansBackgroundBrush: Brush = Brush.verticalGradient(
    listOf(CarpathiansColors.BackgroundTop, CarpathiansColors.BackgroundBottom)
)

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
