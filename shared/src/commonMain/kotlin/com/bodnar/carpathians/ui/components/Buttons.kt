package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.ic_arrow_forward
import com.bodnar.carpathians.theme.CarpathiansButtonBrush
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes
import org.jetbrains.compose.resources.painterResource

/** Базова кнопка з ефектом scale(1.05) при наведенні/натисканні, як у дизайні. */
@Composable
private fun ScalingButton(
    onClick: () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    background: Modifier,
    borderColor: androidx.compose.ui.graphics.Color?,
    contentPadding: PaddingValues,
    content: @Composable RowScope.() -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (hovered || pressed) 1.03f else 1f)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(12.dp, shape, clip = false)
            .clip(shape)
            .then(background)
            .then(
                if (borderColor != null) {
                    Modifier.border(1.dp, if (hovered) CarpathiansColors.Primary else borderColor, shape)
                } else Modifier
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

/** .route-button — темна напівпрозора кнопка з тонкою рамкою. */
@Composable
fun RouteButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = false,
) {
    ScalingButton(
        onClick = onClick,
        shape = CarpathiansShapes.RouteButton,
        modifier = modifier,
        background = Modifier.background(CarpathiansColors.Overlay),
        borderColor = CarpathiansColors.GlassBorderStrong,
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
    ) {
        FitText(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 20.sp),
            color = CarpathiansColors.White,
            minFontSize = 13.sp,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (showArrow) {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = CarpathiansColors.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** .btn-login-custom — зелена градієнтна кнопка-пігулка. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    large: Boolean = false,
    loading: Boolean = false,
) {
    ScalingButton(
        onClick = { if (!loading) onClick() },
        shape = CarpathiansShapes.Pill,
        modifier = modifier,
        background = Modifier.background(CarpathiansButtonBrush),
        borderColor = null,
        contentPadding = if (large) {
            PaddingValues(horizontal = 40.dp, vertical = 16.dp)
        } else {
            PaddingValues(horizontal = 22.dp, vertical = 8.dp)
        },
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = CarpathiansColors.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(if (large) 24.dp else 18.dp),
            )
        } else {
            FitText(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = if (large) 20.sp else 16.sp),
                color = CarpathiansColors.White,
                minFontSize = 12.sp,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}
