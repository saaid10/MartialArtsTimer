package com.saee.combattimer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.ui.theme.CombatColors
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

/** Thick black manga-ink border, for cards/buttons/panels. */
fun inkBorder(width: Dp = 3.dp, color: Color = CombatColors.Ink): BorderStroke = BorderStroke(width, color)

/**
 * Radiating white lines bursting from a center point, like a manga cover's
 * impact flash behind the main figure. Meant to sit behind a focal panel.
 */
@Composable
fun SpeedLineBurst(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White,
    lineCount: Int = 56,
    baseAlpha: Float = 0.35f
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = hypot(size.width, size.height) / 2f
        val random = Random(BURST_SEED)
        repeat(lineCount) { i ->
            val angle = (2.0 * Math.PI * i / lineCount).toFloat()
            val innerRadius = maxRadius * 0.10f
            val outerRadius = maxRadius * (0.55f + random.nextFloat() * 0.5f)
            val start = Offset(center.x + cos(angle) * innerRadius, center.y + sin(angle) * innerRadius)
            val end = Offset(center.x + cos(angle) * outerRadius, center.y + sin(angle) * outerRadius)
            val strokeWidth = maxRadius * (0.006f + random.nextFloat() * 0.012f)
            drawLine(
                color = lineColor.copy(alpha = baseAlpha * (0.5f + random.nextFloat() * 0.5f)),
                start = start,
                end = end,
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private const val BURST_SEED = 42L

/** A boxing-ring rope divider, drawn with a black ink outline like a comic panel border. */
@Composable
fun RingRopeDivider(modifier: Modifier = Modifier, height: Dp = 12.dp) {
    Canvas(modifier = modifier.fillMaxWidth().height(height)) {
        val stripeHeight = size.height / 5f
        val colors = listOf(CombatColors.GloveRed, CombatColors.ImpactYellow, CombatColors.GloveRed)
        colors.forEachIndexed { index, color ->
            val y = stripeHeight * (index * 2 + 1)
            drawLine(
                color = CombatColors.Ink,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = stripeHeight * 1.1f
            )
            drawLine(
                color = color,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = stripeHeight * 0.75f
            )
        }
    }
}

/**
 * Bold comic-book lettering: a black-ink stroked outline behind a solid
 * fill color, like the title lettering on a shonen manga cover.
 */
@Composable
fun ComicText(
    text: String,
    fontSize: TextUnit,
    color: Color,
    modifier: Modifier = Modifier,
    outlineColor: Color = CombatColors.Ink,
    outlineWidth: Float = 7f,
    letterSpacing: TextUnit = 0.sp,
    textAlign: TextAlign? = null
) {
    Box(modifier = modifier) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = letterSpacing,
            color = outlineColor,
            textAlign = textAlign,
            style = TextStyle(drawStyle = Stroke(width = outlineWidth, join = StrokeJoin.Round))
        )
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            letterSpacing = letterSpacing,
            color = color,
            textAlign = textAlign
        )
    }
}
