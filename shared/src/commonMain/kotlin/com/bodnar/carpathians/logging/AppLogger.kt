package com.bodnar.carpathians.logging

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.bodnar.carpathians.getPlatform

/**
 * Обгортка над Kermit. Kermit сам обирає платформний вивід:
 * Logcat на Android, System.out на Desktop, console у браузері.
 */
object AppLogger {
    private const val APP_TAG = "Carpathians"
    private var initialized = false

    fun init(minSeverity: Severity = Severity.Debug) {
        if (initialized) return
        Logger.setTag(APP_TAG)
        Logger.setMinSeverity(minSeverity)
        initialized = true
        Logger.i { "Logger initialized on ${getPlatform().name}, min severity: $minSeverity" }
    }

    fun withTag(tag: String): Logger = Logger.withTag(tag)
}
