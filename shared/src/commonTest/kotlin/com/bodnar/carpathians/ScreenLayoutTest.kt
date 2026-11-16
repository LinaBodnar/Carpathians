package com.bodnar.carpathians

import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import com.bodnar.carpathians.ui.adaptive.ScreenLayout
import com.bodnar.carpathians.ui.adaptive.toScreenLayout
import kotlin.test.Test
import kotlin.test.assertEquals

class ScreenLayoutTest {

    private fun layoutFor(widthDp: Float, heightDp: Float): ScreenLayout =
        WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(widthDp, heightDp).toScreenLayout()

    @Test
    fun phonePortraitIsCompact() {
        assertEquals(ScreenLayout.Compact, layoutFor(360f, 760f))
        assertEquals(ScreenLayout.Compact, layoutFor(599f, 900f))
    }

    @Test
    fun tabletPortraitIsMedium() {
        assertEquals(ScreenLayout.Medium, layoutFor(600f, 960f))
        assertEquals(ScreenLayout.Medium, layoutFor(839f, 1100f))
    }

    @Test
    fun wideWindowIsExpanded() {
        assertEquals(ScreenLayout.Expanded, layoutFor(840f, 900f))
        assertEquals(ScreenLayout.Expanded, layoutFor(1280f, 800f))
    }

    @Test
    fun phoneLandscapeUsesTwoPanes() {
        assertEquals(ScreenLayout.Expanded, layoutFor(760f, 360f))
    }
}
