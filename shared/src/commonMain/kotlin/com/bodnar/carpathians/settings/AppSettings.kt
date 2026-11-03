package com.bodnar.carpathians.settings

import com.bodnar.carpathians.logging.AppLogger
import com.russhwolf.settings.Settings

/**
 * Налаштування користувача на основі multiplatform-settings.
 * Settings() з модуля no-arg використовує SharedPreferences (Android),
 * java.util.prefs.Preferences (Desktop) та localStorage (Web).
 */
class AppSettings(
    private val settings: Settings = Settings(),
) {
    private val log = AppLogger.withTag("AppSettings")

    /** Чи пройшов користувач екран знайомства з додатком. */
    var isOnboardingCompleted: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) {
            settings.putBoolean(KEY_ONBOARDING_COMPLETED, value)
            log.d { "onboardingCompleted = $value" }
        }

    /** Остання переглянута сторінка онбордингу (щоб продовжити з того ж місця). */
    var onboardingPage: Int
        get() = settings.getInt(KEY_ONBOARDING_PAGE, 0)
        set(value) = settings.putInt(KEY_ONBOARDING_PAGE, value)

    /** Мова, обрана користувачем ("uk" / "en"); null — мова системи. */
    var appLanguage: String?
        get() = settings.getStringOrNull(KEY_APP_LANGUAGE)
        set(value) {
            if (value == null) settings.remove(KEY_APP_LANGUAGE) else settings.putString(KEY_APP_LANGUAGE, value)
            log.d { "appLanguage = $value" }
        }

    /** Кількість запусків додатка. */
    val launchCount: Int
        get() = settings.getInt(KEY_LAUNCH_COUNT, 0)

    fun registerLaunch(): Int {
        val count = launchCount + 1
        settings.putInt(KEY_LAUNCH_COUNT, count)
        log.i { "App launch #$count" }
        return count
    }

    fun clear() {
        settings.clear()
        log.w { "All settings were cleared" }
    }

    companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_ONBOARDING_PAGE = "onboarding_page"
        const val KEY_LAUNCH_COUNT = "launch_count"
        const val KEY_APP_LANGUAGE = "app_language"
    }
}
