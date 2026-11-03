package com.bodnar.carpathians

import com.bodnar.carpathians.settings.AppSettings
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppSettingsTest {

    @Test
    fun onboardingIsNotCompletedByDefault() {
        val settings = AppSettings(MapSettings())
        assertFalse(settings.isOnboardingCompleted)
    }

    @Test
    fun onboardingFlagIsPersisted() {
        val storage = MapSettings()
        AppSettings(storage).isOnboardingCompleted = true
        assertTrue(AppSettings(storage).isOnboardingCompleted)
    }

    @Test
    fun launchCountIncrements() {
        val settings = AppSettings(MapSettings())
        settings.registerLaunch()
        assertEquals(2, settings.registerLaunch())
    }
}
