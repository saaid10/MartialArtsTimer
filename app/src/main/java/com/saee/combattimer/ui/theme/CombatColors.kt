package com.saee.combattimer.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Shonen-manga fight-poster palette: deep electric blue, punchy glove red,
 * bright impact yellow, and thick black ink outlines on everything — the
 * Hajime no Ippo cover-art look, not a muted/vintage one.
 */
object CombatColors {
    val Ink = Color(0xFF0A0A0A)
    val Night = Color(0xFF0B1020)
    val PanelNavy = Color(0xFF121A33)
    val PanelNavyAlt = Color(0xFF1A2547)

    val BurstBlue = Color(0xFF1746C4)
    val BurstBlueLight = Color(0xFF4B7BFF)
    val BurstBlueDark = Color(0xFF0C2A80)

    val GloveRed = Color(0xFFE31E24)
    val GloveRedDark = Color(0xFF8E1216)

    val ImpactYellow = Color(0xFFFFD400)
    val ImpactYellowDark = Color(0xFFB88A00)

    val Paper = Color(0xFFF5F1E6)

    // Backwards-compatible aliases used by phase logic elsewhere in the app.
    val InkBlack = Night
    val PanelBlack = PanelNavy
    val PanelBlackAlt = PanelNavyAlt
    val RopeGrey = Ink
    val CornerRed = GloveRed
    val CornerRedDim = GloveRedDark
    val CornerAmber = ImpactYellow
    val CornerAmberDim = ImpactYellowDark
    val CornerGreen = BurstBlue
    val CornerGreenDim = BurstBlueDark
    val Bone = Paper
}
