package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes

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
        Snackbar(
            snackbarData = data,
            shape = CarpathiansShapes.Chip,
            containerColor = CarpathiansColors.MenuGlass,
            contentColor = CarpathiansColors.White,
            actionColor = CarpathiansColors.Primary,
            dismissActionContentColor = CarpathiansColors.TextNav,
            modifier = Modifier
                .widthIn(max = 520.dp)
                .border(
                    1.dp,
                    if (isError) CarpathiansColors.Danger else CarpathiansColors.Primary,
                    CarpathiansShapes.Chip,
                ),
        )
    }
}
