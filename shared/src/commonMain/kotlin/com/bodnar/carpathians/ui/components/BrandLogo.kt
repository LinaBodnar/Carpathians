package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.logo
import com.bodnar.carpathians.theme.CarpathiansColors
import com.bodnar.carpathians.theme.brandFontFamily
import org.jetbrains.compose.resources.painterResource

/** text-shadow: 0 2px 4px rgba(0,0,0,0.8) */
val TextShadow = Shadow(color = Color.Black.copy(alpha = 0.8f), offset = Offset(0f, 4f), blurRadius = 8f)

/** Логотип з хедера дизайну: іконка гори + "Carpathians". */
@Composable
fun BrandLogo(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = null,
            modifier = Modifier.size(32.dp).padding(end = 6.dp),
        )
        Text(
            text = "Carpathians",
            style = TextStyle(
                fontFamily = brandFontFamily(),
                fontSize = 22.sp,
                color = CarpathiansColors.White,
                shadow = TextShadow,
            ),
        )
    }
}

/** Великий заголовок "ADVENTURE" з hero-секції. */
@Composable
fun AdventureTitle(modifier: Modifier = Modifier, fontSize: Int = 64) {
    Text(
        text = "ADVENTURE",
        modifier = modifier,
        style = TextStyle(
            fontFamily = brandFontFamily(),
            fontSize = fontSize.sp,
            letterSpacing = 4.sp,
            color = CarpathiansColors.White,
            shadow = TextShadow,
        ),
    )
}
