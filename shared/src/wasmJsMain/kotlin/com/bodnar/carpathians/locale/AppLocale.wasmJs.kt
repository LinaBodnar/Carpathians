package com.bodnar.carpathians.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale

/** Глобальний window браузера; __customLocale читає скрипт у index.html. */
external object window {
    var __customLocale: String?
}

actual object LocalAppLocale {
    private val LocalLocale = staticCompositionLocalOf { Locale.current }

    actual val current: String
        @Composable get() = LocalLocale.current.toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        window.__customLocale = value?.replace('_', '-')
        return LocalLocale.provides(Locale.current)
    }
}
