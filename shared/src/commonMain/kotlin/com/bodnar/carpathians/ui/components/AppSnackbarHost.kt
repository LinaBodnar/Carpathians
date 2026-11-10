package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.snackbar_dismiss
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import org.jetbrains.compose.resources.stringResource

/** Повідомлення снекбара з ознакою помилки (червона рамка) чи успіху (зелена). */
private data class AppSnackbarVisuals(
    override val message: String,
    val isError: Boolean,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = true,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals

suspend fun SnackbarHostState.showMessage(message: String, isError: Boolean = false) {
    currentSnackbarData?.dismiss()
    showSnackbar(
        AppSnackbarVisuals(
            message = message,
            isError = isError,
            duration = if (isError) SnackbarDuration.Short else SnackbarDuration.Long,
        )
    )
}

/** Снекбар у стилі "скляного" меню дизайну. */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val isError = (data.visuals as? AppSnackbarVisuals)?.isError ?: false
        val accent = if (isError) CarpathiansColors.Danger else CarpathiansColors.Primary
        Row(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .shadow(12.dp, CarpathiansShapes.Chip)
                .clip(CarpathiansShapes.Chip)
                .background(CarpathiansColors.MenuGlass)
                .border(1.dp, accent, CarpathiansShapes.Chip)
                .padding(start = 14.dp, end = 6.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(accent))
            Spacer(Modifier.width(10.dp))
            Text(
                text = data.visuals.message,
                style = MaterialTheme.typography.bodyMedium,
                color = CarpathiansColors.White,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "✕",
                style = MaterialTheme.typography.titleMedium,
                color = CarpathiansColors.TextNav,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(Res.string.snackbar_dismiss),
                    ) { data.dismiss() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
    }
}
