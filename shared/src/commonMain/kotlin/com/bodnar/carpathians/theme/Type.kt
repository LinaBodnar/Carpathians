package com.bodnar.carpathians.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import carpathians.shared.generated.resources.Res
import carpathians.shared.generated.resources.arimo_bold
import carpathians.shared.generated.resources.arimo_regular
import carpathians.shared.generated.resources.dm_serif_text_regular
import carpathians.shared.generated.resources.playfair_display_bold
import carpathians.shared.generated.resources.playfair_display_regular
import org.jetbrains.compose.resources.Font

/**
 * DM Serif Text — фірмовий шрифт дизайну ("ADVENTURE", логотип).
 * Він містить лише латиницю, тому для заголовків українською
 * використовується Playfair Display — близький за характером serif з кирилицею.
 */
@Composable
fun brandFontFamily(): FontFamily = FontFamily(
    Font(Res.font.dm_serif_text_regular, FontWeight.Normal),
)

@Composable
fun serifFontFamily(): FontFamily = FontFamily(
    Font(Res.font.playfair_display_regular, FontWeight.Normal),
    Font(Res.font.playfair_display_bold, FontWeight.Bold),
)

/** Arimo — метрично сумісний з Arial (--font-sans у дизайні), з кирилицею. */
@Composable
fun sansFontFamily(): FontFamily = FontFamily(
    Font(Res.font.arimo_regular, FontWeight.Normal),
    Font(Res.font.arimo_bold, FontWeight.Bold),
)

@Composable
fun carpathiansTypography(): Typography {
    val serif = serifFontFamily()
    val sans = sansFontFamily()
    val text = CarpathiansColors.TextMain

    return Typography(
        displayLarge = TextStyle(fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 56.sp, lineHeight = 60.sp, letterSpacing = 2.sp, color = text),
        displayMedium = TextStyle(fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 42.sp, lineHeight = 48.sp, letterSpacing = 2.sp, color = text),
        displaySmall = TextStyle(fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = 1.sp, color = text),
        headlineLarge = TextStyle(fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = 1.sp, color = text),
        headlineMedium = TextStyle(fontFamily = serif, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 32.sp, color = text),
        headlineSmall = TextStyle(fontFamily = serif, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 28.sp, color = text),
        titleLarge = TextStyle(fontFamily = serif, fontWeight = FontWeight.Normal, fontSize = 22.sp, lineHeight = 28.sp, color = text),
        titleMedium = TextStyle(fontFamily = serif, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 24.sp, color = text),
        titleSmall = TextStyle(fontFamily = serif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp, color = text),
        bodyLarge = TextStyle(fontFamily = sans, fontSize = 17.sp, lineHeight = 26.sp, color = text),
        bodyMedium = TextStyle(fontFamily = sans, fontSize = 15.sp, lineHeight = 22.sp, color = text),
        bodySmall = TextStyle(fontFamily = sans, fontSize = 13.sp, lineHeight = 18.sp, color = text),
        labelLarge = TextStyle(fontFamily = serif, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 22.sp, letterSpacing = 1.sp),
        labelMedium = TextStyle(fontFamily = sans, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.5.sp),
        labelSmall = TextStyle(fontFamily = sans, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    )
}
