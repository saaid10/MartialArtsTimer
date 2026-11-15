package com.saee.combattimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.ui.theme.CombatType
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/** Shared corner radius for panels/buttons — crisp, nearly square. */
val PanelShape: Shape = RoundedCornerShape(6.dp)

/** Badge shape: top-left and bottom-right corners cut at 45°. */
class ChamferShape(private val cut: Dp = 6.dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val c = with(density) { cut.toPx() }
        val path = Path().apply {
            moveTo(c, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - c)
            lineTo(size.width - c, size.height)
            lineTo(0f, size.height)
            lineTo(0f, c)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Comic panel: fill + black ink border + hard (unblurred) offset shadow.
 * Apply padding AFTER this modifier so content sits inside the panel.
 */
fun Modifier.inkPanel(
    color: Color,
    shadow: Dp = 4.dp,
    border: Dp = 2.dp,
    shape: Shape = PanelShape
): Modifier = this
    .drawBehind {
        val s = shadow.toPx()
        val outline = shape.createOutline(size, layoutDirection, this)
        translate(s, s) { drawOutline(outline, CombatColors.Ink) }
    }
    .clip(shape)
    .background(color)
    .border(border, CombatColors.Ink, shape)

/** Screentone dots. When [fade] is true they fade in from left to right (accent corner look). */
fun Modifier.halftone(
    color: Color = CombatColors.PanelNavyAlt,
    spacing: Dp = 9.dp,
    radius: Dp = 1.4.dp,
    fade: Boolean = false
): Modifier = drawBehind {
    val step = spacing.toPx()
    val r = radius.toPx()
    var y = step / 2
    while (y < size.height) {
        var x = step / 2
        while (x < size.width) {
            val alpha = if (fade) (x / size.width).coerceIn(0f, 1f) else 1f
            drawCircle(color.copy(alpha = color.alpha * alpha), r, Offset(x, y))
            x += step
        }
        y += step
    }
}

/** Forward-slanted hazard stripes (used on phase banners and the active round block). */
fun Modifier.diagonalStripes(color: Color, gap: Dp = 18.dp, width: Dp = 7.dp): Modifier = drawBehind {
    val g = gap.toPx()
    val w = width.toPx()
    var x = -size.height
    while (x < size.width + size.height) {
        drawLine(color, Offset(x, size.height), Offset(x + size.height, 0f), strokeWidth = w)
        x += g
    }
}

/** Sunburst wedges radiating from a point (fractions of the box), like a manga impact panel. */
fun Modifier.sunburst(color: Color, rays: Int = 18, centerX: Float = 0.5f, centerY: Float = 0.5f): Modifier = drawBehind {
    val c = Offset(size.width * centerX, size.height * centerY)
    val r = hypot(size.width, size.height)
    val slice = (2 * Math.PI / (rays * 2)).toFloat()
    for (i in 0 until rays) {
        val a0 = slice * i * 2
        val a1 = a0 + slice
        val path = Path().apply {
            moveTo(c.x, c.y)
            lineTo(c.x + cos(a0) * r, c.y + sin(a0) * r)
            lineTo(c.x + cos(a1) * r, c.y + sin(a1) * r)
            close()
        }
        drawPath(path, color)
    }
}

/** Small chamfered tag, e.g. CONFIGURABLE / GOLDEN SCORE. */
@Composable
fun TagBadge(text: String, background: Color, contentColor: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = CombatType.LabelSm,
        color = contentColor,
        maxLines = 1,
        modifier = modifier
            .clip(ChamferShape(5.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/**
 * Primary trigger. Sits on a hard ink shadow and "punches" down into it when pressed:
 * the face moves +3dp while the shadow shrinks by the same amount, so the shadow stays put.
 */
@Composable
fun PunchButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = CombatColors.GloveRed,
    contentColor: Color = CombatColors.Paper,
    minHeight: Dp = 64.dp,
    textStyle: TextStyle = CombatType.HeadlineLg,
    subtitle: String? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shift = if (pressed) 3.dp else 0.dp
    Box(
        modifier = modifier
            .heightIn(min = minHeight)
            .offset(shift, shift)
            .inkPanel(container, shadow = 5.dp - shift, border = 3.dp)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = textStyle.copy(fontStyle = FontStyle.Italic),
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(text = subtitle, style = CombatType.LabelSm, color = contentColor.copy(alpha = 0.75f), maxLines = 1)
            }
        }
    }
}

/** Full-width phase ribbon: FIGHT! / REST / GOLDEN SCORE / TIME! / FINISHED. */
@Composable
fun PhaseBanner(
    title: String,
    caption: String,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .inkPanel(color, shadow = 4.dp, border = 3.dp)
            .diagonalStripes(contentColor.copy(alpha = 0.10f))
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .padding(end = 10.dp)
                .clip(RoundedCornerShape(50))
                .background(if (color == CombatColors.ImpactYellow) CombatColors.Ink else CombatColors.ImpactYellow)
                .size(12.dp)
        )
        Text(
            text = title,
            style = CombatType.HeadlineXl.copy(fontStyle = FontStyle.Italic),
            color = contentColor,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(text = caption, style = CombatType.LabelLg, color = contentColor.copy(alpha = 0.85f), maxLines = 1, textAlign = TextAlign.End)
    }
}

enum class RoundCellState { DONE, ACTIVE, UPCOMING }

/**
 * Segmented round tracker: one numbered block per round. Done = red, current = yellow with
 * hazard stripes, upcoming = dark outline. Wraps to rows of [perRow] for long fights.
 */
@Composable
fun RoundSequence(
    totalRounds: Int,
    stateOf: (round: Int) -> RoundCellState,
    modifier: Modifier = Modifier,
    perRow: Int = 10
) {
    val rows = (1..totalRounds).chunked(perRow)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                row.forEach { round -> RoundCell(round, stateOf(round), Modifier.weight(1f)) }
                // Keep cell width consistent on a short final row.
                if (rows.size > 1 && row.size < perRow) Spacer(Modifier.weight((perRow - row.size).toFloat()))
            }
        }
    }
}

@Composable
private fun RoundCell(round: Int, state: RoundCellState, modifier: Modifier) {
    val (bg, fg) = when (state) {
        RoundCellState.DONE -> CombatColors.GloveRed to CombatColors.Paper
        RoundCellState.ACTIVE -> CombatColors.ImpactYellow to CombatColors.Ink
        RoundCellState.UPCOMING -> CombatColors.PanelNavyAlt to CombatColors.Paper.copy(alpha = 0.45f)
    }
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .then(if (state == RoundCellState.ACTIVE) Modifier.diagonalStripes(CombatColors.Ink.copy(alpha = 0.12f), gap = 10.dp, width = 3.dp) else Modifier)
            .border(1.5.dp, CombatColors.Ink, RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = round.toString(), style = CombatType.HeadlineMd, color = fg, maxLines = 1)
    }
}
