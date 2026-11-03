package com.bodnar.carpathians

import com.bodnar.carpathians.locale.AppLanguage
import com.bodnar.carpathians.settings.AppSettings
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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

    @Test
    fun languageIsSystemByDefaultAndCanBeChanged() {
        val storage = MapSettings()
        val settings = AppSettings(storage)
        assertNull(settings.appLanguage)

        settings.appLanguage = "uk"
        assertEquals("uk", AppSettings(storage).appLanguage)

        settings.appLanguage = null
        assertNull(AppSettings(storage).appLanguage)
    }

    @Test
    fun appLanguageIsParsedFromTag() {
        assertEquals(AppLanguage.Ukrainian, AppLanguage.fromTag("uk_UA"))
        assertEquals(AppLanguage.English, AppLanguage.fromTag("en-US"))
        assertNull(AppLanguage.fromTag("de"))
    }
}
