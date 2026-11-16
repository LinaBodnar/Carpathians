package com.bodnar.carpathians.ui.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.window.core.layout.WindowSizeClass

enum class ScreenLayout { Compact, Medium, Expanded }

@Composable
fun currentWindowSizeClass(): WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

fun WindowSizeClass.toScreenLayout(): ScreenLayout {
    val isMediumWidth = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    val isExpandedWidth = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    val isCompactHeight = !isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
    return when {
        isExpandedWidth -> ScreenLayout.Expanded
        isMediumWidth && isCompactHeight -> ScreenLayout.Expanded
        isMediumWidth -> ScreenLayout.Medium
        else -> ScreenLayout.Compact
    }
}

@Composable
fun currentScreenLayout(): ScreenLayout = currentWindowSizeClass().toScreenLayout()
