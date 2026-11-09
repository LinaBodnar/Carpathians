package com.bodnar.carpathians.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.language_switch_description
import androidx.compose.ui.text.intl.Locale
import com.bodnar.carpathians.locale.AppLanguage
import com.bodnar.carpathians.locale.customAppLocale
import com.bodnar.carpathians.logging.AppLogger
import com.bodnar.carpathians.settings.AppSettings
import org.koin.compose.koinInject
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import org.jetbrains.compose.resources.stringResource

/**
 * Перемикач мови, підключений до налаштувань: зберігає вибір
 * у multiplatform-settings і одразу застосовує його до UI.
 */
@Composable
fun AppLanguageSwitcher(modifier: Modifier = Modifier) {
    val settings = koinInject<AppSettings>()
    val current = AppLanguage.fromTag(customAppLocale)
        ?: AppLanguage.fromTag(Locale.current.language)
        ?: AppLanguage.English
    LanguageSwitcher(
        selected = current,
        onSelect = { language ->
            log.i { "Language changed: ${current.tag} -> ${language.tag}" }
            settings.appLanguage = language.tag
            customAppLocale = language.tag
        },
        modifier = modifier,
    )
}

private val log = AppLogger.withTag("Language")

/** Перемикач мови UA / EN у стилі вкладок маршрутів. */
@Composable
fun LanguageSwitcher(
    selected: AppLanguage,
    onSelect: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(Res.string.language_switch_description)
    Row(
        modifier = modifier
            .semantics { contentDescription = description }
            .clip(CarpathiansShapes.Pill)
            .background(CarpathiansColors.Overlay)
            .border(1.dp, CarpathiansColors.GlassBorderStrong, CarpathiansShapes.Pill)
            .padding(3.dp),
    ) {
        AppLanguage.entries.forEach { language ->
            val isSelected = language == selected
            val background by animateColorAsState(
                if (isSelected) CarpathiansColors.Primary else Color.Transparent
            )
            Box(
                Modifier
                    .clip(CarpathiansShapes.Pill)
                    .background(background)
                    .clickable(role = Role.Tab) { if (!isSelected) onSelect(language) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = language.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) CarpathiansColors.White else CarpathiansColors.TextNav,
                )
            }
        }
    }
}
