package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.CarpathiansShapes

/** .glass-panel — напівпрозора панель зі світлою рамкою. */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CarpathiansShapes.Panel)
            .background(CarpathiansColors.Overlay)
            .border(1.dp, CarpathiansColors.GlassBorder, CarpathiansShapes.Panel)
            .padding(20.dp),
        content = content,
    )
}

/** Чіп у стилі вкладок маршрутів (.route-list .nav-link). */
@Composable
fun GlassChip(
    text: String,
    modifier: Modifier = Modifier,
    dotColor: Color? = null,
) {
    Row(
        modifier = modifier
            .clip(CarpathiansShapes.Chip)
            .background(CarpathiansColors.GlassHover)
            .border(1.dp, CarpathiansColors.GlassBorder, CarpathiansShapes.Chip)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dotColor != null) {
            Box(
                Modifier
                    .padding(end = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = CarpathiansColors.TextPleasant,
        )
    }
}
