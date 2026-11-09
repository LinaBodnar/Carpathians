package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.border
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes

/** Снекбар у стилі "скляного" меню дизайну. */
@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        Snackbar(
            snackbarData = data,
            shape = CarpathiansShapes.Chip,
            containerColor = CarpathiansColors.MenuGlass,
            contentColor = CarpathiansColors.White,
            actionColor = CarpathiansColors.Primary,
            modifier = Modifier.border(
                1.dp,
                if (isError) CarpathiansColors.Danger else CarpathiansColors.Primary,
                CarpathiansShapes.Chip,
            ),
        )
    }
}
