package com.bodnar.carpathians.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val tag: String, val label: String) {
    Ukrainian("uk", "UA"),
    English("en", "EN");

    companion object {
        fun fromTag(tag: String?): AppLanguage? =
            entries.firstOrNull { tag != null && tag.lowercase().startsWith(it.tag) }
    }
}

var customAppLocale by mutableStateOf<String?>(null)

expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

@Composable
fun AppEnvironment(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppLocale provides customAppLocale) {
        key(customAppLocale) {
            content()
        }
    }
}
