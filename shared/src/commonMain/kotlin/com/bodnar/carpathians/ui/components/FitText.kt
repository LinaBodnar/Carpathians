package com.bodnar.carpathians.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/**
 * Однорядковий текст, який зменшує шрифт, щоб поміститися в доступну ширину
 * (замість переносу на другий рядок на вузьких екранах).
 * Шрифт підбирається від `style.fontSize` до [minFontSize] з кроком 1sp;
 * трикрапка з'являється, лише якщо не вміщується навіть мінімальний розмір.
 */
@Composable
fun FitText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.Unspecified,
    minFontSize: TextUnit = 10.sp,
) {
    val baseStyle = style.merge(color = color, textAlign = textAlign)
    val maxFontSize = if (style.fontSize.isSpecified) style.fontSize else 16.sp
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(modifier) {
        val maxWidth = constraints.maxWidth
        val fontSize = remember(text, baseStyle, maxWidth, density, minFontSize) {
            if (maxWidth == Constraints.Infinity) return@remember maxFontSize
            var size = maxFontSize.value
            while (size > minFontSize.value) {
                val result = measurer.measure(
                    text = text,
                    style = baseStyle.copy(fontSize = size.sp),
                    maxLines = 1,
                    softWrap = false,
                    density = density,
                )
                if (result.size.width <= maxWidth) break
                size -= 1f
            }
            size.coerceAtLeast(minFontSize.value).sp
        }
        // Для вирівнювання по центру / праворуч текст має зайняти всю доступну ширину
        val aligned = (textAlign == TextAlign.Center || textAlign == TextAlign.End) &&
            maxWidth != Constraints.Infinity
        BasicText(
            text = text,
            modifier = if (aligned) Modifier.fillMaxWidth() else Modifier,
            style = baseStyle.copy(fontSize = fontSize),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
