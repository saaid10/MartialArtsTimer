package com.saee.combattimer.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Type system from the Stitch design: tall condensed display face for numbers/titles,
 * clean geometric sans for labels. Uses built-in device fonts so no font files are needed;
 * swap in Bebas Neue / Space Grotesk (res/font) here later and every screen picks it up.
 */
object CombatType {
    val Display: FontFamily = FontFamily(
        Font(DeviceFontFamilyName("sans-serif-condensed"), FontWeight.Bold),
        Font(DeviceFontFamilyName("sans-serif-condensed"), FontWeight.Bold, FontStyle.Italic)
    )
    val Body: FontFamily = FontFamily.SansSerif

    val Clock = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 104.sp, letterSpacing = 2.sp)
    val HeadlineXl = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 40.sp, letterSpacing = 1.5.sp)
    val HeadlineLg = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.sp)
    val HeadlineMd = TextStyle(fontFamily = Display, fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 0.8.sp)
    val BodyLg = TextStyle(fontFamily = Body, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    val LabelLg = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
    val LabelSm = TextStyle(fontFamily = Body, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
}
