package com.saee.combattimer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

/**
 * A digital-clock style readout built from drawn segments (7 per digit, plus a
 * colon), instead of a text glyph. Unlit segments are drawn faintly so the
 * digits read like an old ring/scoreboard clock rather than plain text.
 */
@Composable
fun SevenSegmentDisplay(
    value: String,
    modifier: Modifier = Modifier,
    digitHeight: Dp = 88.dp,
    onColor: Color = Color(0xFFFF3B30),
    offColor: Color = onColor.copy(alpha = 0.12f)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(digitHeight * 0.12f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        value.forEach { char ->
            when {
                char.isDigit() -> SevenSegmentDigit(char, digitHeight, onColor, offColor)
                char == ':' -> ColonSeparator(digitHeight, onColor)
                else -> Spacer(Modifier.width(digitHeight * 0.22f))
            }
        }
    }
}

private val SEGMENTS: Map<Char, Set<Char>> = mapOf(
    '0' to setOf('a', 'b', 'c', 'd', 'e', 'f'),
    '1' to setOf('b', 'c'),
    '2' to setOf('a', 'b', 'g', 'e', 'd'),
    '3' to setOf('a', 'b', 'g', 'c', 'd'),
    '4' to setOf('f', 'g', 'b', 'c'),
    '5' to setOf('a', 'f', 'g', 'c', 'd'),
    '6' to setOf('a', 'f', 'g', 'e', 'c', 'd'),
    '7' to setOf('a', 'b', 'c'),
    '8' to setOf('a', 'b', 'c', 'd', 'e', 'f', 'g'),
    '9' to setOf('a', 'b', 'c', 'd', 'f', 'g')
)

@Composable
private fun SevenSegmentDigit(digit: Char, height: Dp, onColor: Color, offColor: Color) {
    val active = SEGMENTS[digit] ?: emptySet()
    Canvas(modifier = Modifier.height(height).width(height * 0.56f)) {
        drawDigit(active, onColor, offColor)
    }
}

private fun DrawScope.drawDigit(active: Set<Char>, onColor: Color, offColor: Color) {
    val w = size.width
    val h = size.height
    val thickness = w * 0.22f
    val inset = thickness * 0.55f

    val x0 = inset
    val x2 = w - inset
    val y0 = inset
    val y2 = h - inset
    val ym = h / 2f

    // 6-cornered hexagon segment: pointed tips at the endpoints, flat shoulders
    // between — the sharp LED-clock shape, not a rounded stroke.
    fun segment(key: Char, start: Offset, end: Offset) {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val len = hypot(dx, dy).takeIf { it != 0f } ?: 1f
        val ux = dx / len
        val uy = dy / len
        val px = -uy
        val py = ux
        val half = thickness / 2f

        val innerStart = Offset(start.x + ux * half, start.y + uy * half)
        val innerEnd = Offset(end.x - ux * half, end.y - uy * half)

        val hexagon = Path().apply {
            moveTo(start.x, start.y)
            lineTo(innerStart.x + px * half, innerStart.y + py * half)
            lineTo(innerEnd.x + px * half, innerEnd.y + py * half)
            lineTo(end.x, end.y)
            lineTo(innerEnd.x - px * half, innerEnd.y - py * half)
            lineTo(innerStart.x - px * half, innerStart.y - py * half)
            close()
        }
        drawPath(hexagon, color = if (key in active) onColor else offColor)
    }

    segment('a', Offset(x0, y0), Offset(x2, y0))
    segment('b', Offset(x2, y0), Offset(x2, ym))
    segment('c', Offset(x2, ym), Offset(x2, y2))
    segment('d', Offset(x0, y2), Offset(x2, y2))
    segment('e', Offset(x0, ym), Offset(x0, y2))
    segment('f', Offset(x0, y0), Offset(x0, ym))
    segment('g', Offset(x0, ym), Offset(x2, ym))
}

@Composable
private fun ColonSeparator(height: Dp, onColor: Color) {
    val dotSize = height * 0.11f
    Column(
        modifier = Modifier.height(height),
        verticalArrangement = Arrangement.Center
    ) {
        Dot(dotSize, onColor)
        Spacer(Modifier.height(height * 0.18f))
        Dot(dotSize, onColor)
    }
}

@Composable
private fun Dot(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val diamond = Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(w, h / 2f)
            lineTo(w / 2f, h)
            lineTo(0f, h / 2f)
            close()
        }
        drawPath(diamond, color = color)
    }
}
