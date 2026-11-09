package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.back
import carpathians.shared.generated.resources.ic_arrow_forward
import com.bodnar.carpathians.domain.routes.LocalizedText
import com.bodnar.carpathians.locale.AppLanguage
import com.bodnar.carpathians.locale.LocalAppLocale
import com.bodnar.carpathians.theme.CarpathiansColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Верхня панель внутрішніх екранів: "Назад", заголовок, дії. */
@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CarpathiansColors.OverlayStrong)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = stringResource(Res.string.back),
                tint = CarpathiansColors.White,
                modifier = Modifier.size(22.dp).rotate(180f),
            )
        }
        Spacer(Modifier.width(4.dp))
        FitText(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = CarpathiansColors.White,
            minFontSize = 14.sp,
            modifier = Modifier.weight(1f),
        )
        actions()
    }
}

/** Мова інтерфейсу, що діє зараз (обрана в застосунку або системна). */
@Composable
fun currentAppLanguage(): AppLanguage = AppLanguage.fromTag(LocalAppLocale.current) ?: AppLanguage.English

/** Текст контенту відповідною мовою. */
@Composable
fun LocalizedText.localized(): String = get(currentAppLanguage() == AppLanguage.Ukrainian)
