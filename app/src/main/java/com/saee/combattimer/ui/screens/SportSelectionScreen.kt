package com.saee.combattimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.model.SportType
import com.saee.combattimer.ui.components.ComicText
import com.saee.combattimer.ui.components.RingRopeDivider
import com.saee.combattimer.ui.components.inkBorder
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.util.formatClock

@Composable
fun SportSelectionScreen(
    presets: List<SportPreset>,
    onPresetSelected: (SportPreset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CombatColors.Night)
            .padding(20.dp)
    ) {
        ComicText(
            text = "COMBAT TIMER",
            fontSize = 32.sp,
            color = CombatColors.ImpactYellow,
            outlineWidth = 8f,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
        )
        Text(
            text = "Pick a discipline. Fixed rules start immediately — Interval Setting and Boxing Pro let you configure first.",
            fontSize = 14.sp,
            color = CombatColors.Paper.copy(alpha = 0.65f),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        RingRopeDivider(modifier = Modifier.padding(bottom = 18.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(presets) { preset ->
                SportCard(preset = preset, onClick = { onPresetSelected(preset) })
            }
        }
    }
}

private fun accentFor(type: SportType): Color = when (type) {
    SportType.MMA_AMATEUR, SportType.MMA_PRO_REGULAR, SportType.MMA_PRO_TITLE -> CombatColors.GloveRed
    SportType.BOXING_AMATEUR, SportType.BOXING_PROFESSIONAL -> CombatColors.ImpactYellow
    SportType.JUDO -> CombatColors.BurstBlueLight
    SportType.INTERVAL -> CombatColors.Paper
    SportType.SHARK_TANK -> CombatColors.GloveRedDark
}

@Composable
private fun SportCard(preset: SportPreset, onClick: () -> Unit) {
    val accent = accentFor(preset.type)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CombatColors.PanelNavy),
        border = inkBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(10.dp)
                    .background(accent)
            )
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                ComicText(
                    text = preset.displayName.uppercase(),
                    fontSize = 18.sp,
                    color = accent,
                    outlineWidth = 5f,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = preset.summary(),
                    fontSize = 13.sp,
                    color = CombatColors.Paper.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Box(modifier = Modifier.fillMaxHeight().padding(end = 16.dp), contentAlignment = Alignment.CenterEnd) {
                Text(text = "›", fontSize = 28.sp, fontWeight = FontWeight.Black, color = accent)
            }
        }
    }
}

private fun SportPreset.summary(): String {
    val roundsPart = if (roundsConfigurable) {
        "${roundsRange.first}-${roundsRange.last} rounds"
    } else {
        "$defaultRounds round${if (defaultRounds > 1) "s" else ""}"
    }
    val restPart = when {
        hasGoldenScore -> ", no rest, golden score"
        restSeconds > 0 -> ", ${formatClock(restSeconds)} rest"
        else -> ", no rest"
    }
    val configurablePart = when {
        fullyConfigurable -> " · configurable"
        roundSecondsConfigurable -> " · configurable"
        roundsConfigurable -> " · rounds configurable"
        else -> ""
    }
    return "$roundsPart × ${formatClock(roundSeconds)}$restPart$configurablePart"
}
