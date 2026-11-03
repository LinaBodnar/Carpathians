package com.bodnar.carpathians.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Радіуси заокруглень з дизайну. */
object CarpathiansShapes {
    val Panel = RoundedCornerShape(24.dp)       // .glass-panel
    val Modal = RoundedCornerShape(20.dp)       // .glass-login-modal, .footer-box
    val RouteButton = RoundedCornerShape(18.dp) // .route-button
    val Card = RoundedCornerShape(16.dp)        // .gallery-item
    val Chip = RoundedCornerShape(12.dp)        // .route-list .nav-link
    val Pill = RoundedCornerShape(50)           // .btn-login-custom, rounded-pill
}

val carpathiansMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = CarpathiansShapes.Chip,
    medium = CarpathiansShapes.Card,
    large = CarpathiansShapes.Modal,
    extraLarge = CarpathiansShapes.Panel,
)
