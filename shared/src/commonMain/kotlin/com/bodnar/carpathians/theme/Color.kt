package com.bodnar.carpathians.theme

import androidx.compose.ui.graphics.Color

/**
 * Палітра Carpathians, перенесена з дизайну (project/style.css).
 */
object CarpathiansColors {
    // --color-primary / --color-primary-dark
    val Primary = Color(0xFF4CAF50)
    val PrimaryDark = Color(0xFF2E7D32)
    val PrimaryDeep = Color(0xFF1B5E20)

    // body background: linear-gradient(#1B2E2B -> #050A09)
    val BackgroundTop = Color(0xFF1B2E2B)
    val BackgroundBottom = Color(0xFF050A09)

    // Текст
    val TextMain = Color(0xFFE0E0E0)       // --color-text-main
    val TextPleasant = Color(0xFFD1D1D1)   // .text-pleasant-white
    val TextMuted = Color(0xFF999999)      // .text-light-muted
    val TextNav = Color(0xFFCCCCCC)        // .nav-center a
    val White = Color(0xFFFFFFFF)

    // "Скляні" поверхні (.glass-panel, .gear-card, .route-list .nav-link)
    val GlassSubtle = Color(0x08FFFFFF)    // rgba(255,255,255,0.03)
    val Glass = Color(0x0DFFFFFF)          // rgba(255,255,255,0.05)
    val GlassStrong = Color(0x14FFFFFF)    // rgba(255,255,255,0.08)
    val GlassHover = Color(0x26FFFFFF)     // rgba(255,255,255,0.15)
    val GlassBorder = Color(0x1AFFFFFF)    // rgba(255,255,255,0.1)
    val GlassBorderStrong = Color(0x33FFFFFF) // rgba(255,255,255,0.2)

    // Темні напівпрозорі шари (.route-button, header, модальні вікна)
    val Overlay = Color(0x99000000)        // rgba(0,0,0,0.6)
    val OverlayStrong = Color(0xB3000000)  // rgba(0,0,0,0.7)
    val MenuGlass = Color(0xF21B2E2B)      // rgba(27,46,43,0.95)

    // Акценти іконок спорядження
    val Warning = Color(0xFFFFC107)
    val Info = Color(0xFF0DCAF0)
    val Danger = Color(0xFFDC3545)
    val Blue = Color(0xFF0D6EFD)
}
