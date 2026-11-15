package com.saee.combattimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.model.SportType
import com.saee.combattimer.ui.components.ChamferShape
import com.saee.combattimer.ui.components.TagBadge
import com.saee.combattimer.ui.components.halftone
import com.saee.combattimer.ui.components.inkPanel
import com.saee.combattimer.ui.components.sunburst
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.ui.theme.CombatType
import com.saee.combattimer.util.formatClock

@Composable
fun SportSelectionScreen(
    presets: List<SportPreset>,
    onPresetSelected: (SportPreset) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CombatColors.Night),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { AppHeader() }
        item { HeroPanel(Modifier.padding(top = 4.dp, bottom = 8.dp)) }
        items(presets, key = { it.type }) { preset ->
            SportCard(preset = preset, onClick = { onPresetSelected(preset) })
        }
    }
}

@Composable
private fun AppHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(ChamferShape(8.dp))
                .background(CombatColors.GloveRed),
            contentAlignment = Alignment.Center
        ) {
            Text("CT", style = CombatType.HeadlineMd.copy(fontStyle = FontStyle.Italic), color = CombatColors.Paper)
        }
        Column(Modifier.padding(start = 12.dp)) {
            Text("COMBAT TIMER", style = CombatType.HeadlineLg.copy(fontStyle = FontStyle.Italic), color = CombatColors.Paper)
            Text("ROUND TIMER", style = CombatType.LabelSm, color = CombatColors.ImpactYellow)
        }
    }
}

@Composable
private fun HeroPanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .inkPanel(CombatColors.PanelNavy, shadow = 5.dp, border = 3.dp)
            .sunburst(CombatColors.GloveRed.copy(alpha = 0.18f), centerX = 0.88f, centerY = 0.5f)
            .halftone(CombatColors.PanelNavyAlt, fade = true)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Column {
            Text("PICK YOUR FIGHT", style = CombatType.HeadlineXl.copy(fontStyle = FontStyle.Italic), color = CombatColors.Paper)
            Text(
                "FIXED RULES START INSTANTLY",
                style = CombatType.LabelLg,
                color = CombatColors.ImpactYellow,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun accentFor(type: SportType): Color = when (type) {
    SportType.MMA_AMATEUR, SportType.MMA_PRO_REGULAR, SportType.MMA_PRO_TITLE, SportType.SHARK_TANK -> CombatColors.GloveRed
    SportType.BOXING_AMATEUR, SportType.BOXING_PROFESSIONAL -> CombatColors.BurstBlueLight
    SportType.JUDO, SportType.INTERVAL -> CombatColors.ImpactYellow
}

private data class Tag(val text: String, val bg: Color, val fg: Color)

private fun tagFor(preset: SportPreset): Tag? = when {
    preset.type == SportType.SHARK_TANK -> Tag("NON-STOP", CombatColors.GloveRed, CombatColors.Paper)
    preset.hasGoldenScore -> Tag("GOLDEN SCORE", CombatColors.ImpactYellow, CombatColors.Ink)
    preset.isConfigurable -> Tag("CONFIGURABLE", CombatColors.ImpactYellow, CombatColors.Ink)
    preset.type == SportType.MMA_PRO_TITLE -> Tag("TITLE", CombatColors.PanelNavyAlt, CombatColors.ImpactYellow)
    else -> null
}

@Composable
private fun SportCard(preset: SportPreset, onClick: () -> Unit) {
    val accent = accentFor(preset.type)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shift = if (pressed) 3.dp else 0.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .offset(shift, shift)
            .inkPanel(CombatColors.PanelNavy, shadow = 4.dp - shift)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.fillMaxHeight().width(8.dp).background(accent))
        Column(
            Modifier
                .weight(1f)
                .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 8.dp)
        ) {
            tagFor(preset)?.let { tag ->
                TagBadge(tag.text, tag.bg, tag.fg, Modifier.padding(bottom = 6.dp))
            }
            Text(
                text = preset.displayName.uppercase(),
                style = CombatType.HeadlineMd,
                color = CombatColors.Paper,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = preset.summary(),
                style = CombatType.BodyLg,
                color = CombatColors.Paper.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .padding(end = 14.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CombatColors.PanelNavyAlt),
            contentAlignment = Alignment.Center
        ) {
            Text("›", style = CombatType.HeadlineLg, color = accent)
        }
    }
}

private fun SportPreset.summary(): String = when {
    fullyConfigurable -> "Custom rounds, work & rest"
    hasGoldenScore -> "${formatClock(roundSeconds)} + Golden Score"
    restSeconds <= 0 -> "Back-to-back rounds • No rest"
    roundsConfigurable -> "${roundsRange.first}–${roundsRange.last} rounds × ${formatClock(roundSeconds)} • Rest ${formatClock(restSeconds)}"
    else -> "$defaultRounds × ${formatClock(roundSeconds)} • Rest ${formatClock(restSeconds)}"
}
