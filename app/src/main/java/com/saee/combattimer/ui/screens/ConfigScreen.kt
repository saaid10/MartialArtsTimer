package com.saee.combattimer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.ui.components.PunchButton
import com.saee.combattimer.ui.components.TagBadge
import com.saee.combattimer.ui.components.halftone
import com.saee.combattimer.ui.components.inkPanel
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.ui.theme.CombatType
import com.saee.combattimer.util.formatClock

private const val MIN_DURATION_SECONDS = 5
private const val MAX_DURATION_SECONDS = 20 * 60
private const val DURATION_STEP = 5

@Composable
fun ConfigScreen(
    preset: SportPreset,
    onBack: () -> Unit,
    onStart: (rounds: Int, roundSeconds: Int, restSeconds: Int) -> Unit
) {
    var rounds by remember { mutableIntStateOf(preset.defaultRounds) }
    var roundSeconds by remember { mutableIntStateOf(preset.roundSeconds) }
    var restSeconds by remember { mutableIntStateOf(preset.restSeconds) }

    val roundsRange = if (preset.fullyConfigurable) 1..30 else preset.roundsRange
    val roundsStepper: @Composable () -> Unit = {
        StepperCard(
            title = "TOTAL ROUNDS",
            subtitle = "HOW MANY ROUNDS",
            display = rounds.toString(),
            unit = "ROUNDS",
            plusColor = CombatColors.GloveRed,
            onMinus = { rounds = (rounds - 1).coerceIn(roundsRange.first, roundsRange.last) },
            onPlus = { rounds = (rounds + 1).coerceIn(roundsRange.first, roundsRange.last) }
        )
    }
    val roundTimeStepper: @Composable () -> Unit = {
        StepperCard(
            title = "ROUND TIME",
            subtitle = "FIGHT DURATION",
            display = formatClock(roundSeconds),
            unit = "MIN : SEC",
            plusColor = CombatColors.GloveRed,
            tag = "FIGHT",
            tagColor = CombatColors.GloveRed,
            onMinus = { roundSeconds = (roundSeconds - DURATION_STEP).coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS) },
            onPlus = { roundSeconds = (roundSeconds + DURATION_STEP).coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CombatColors.Night)
            .padding(20.dp)
    ) {
        ConfigHeader(title = preset.displayName.uppercase(), onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp, bottom = 20.dp, end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            when {
                preset.fullyConfigurable -> {
                    roundsStepper()
                    roundTimeStepper()
                    StepperCard(
                        title = "REST TIME",
                        subtitle = "CORNER RECOVERY",
                        display = formatClock(restSeconds),
                        unit = "MIN : SEC",
                        plusColor = CombatColors.BurstBlue,
                        tag = "REST",
                        tagColor = CombatColors.BurstBlue,
                        onMinus = { restSeconds = (restSeconds - DURATION_STEP).coerceIn(0, MAX_DURATION_SECONDS) },
                        onPlus = { restSeconds = (restSeconds + DURATION_STEP).coerceIn(0, MAX_DURATION_SECONDS) }
                    )
                }
                preset.roundSecondsConfigurable -> {
                    roundsStepper()
                    roundTimeStepper()
                    InfoStrip("NO REST  •  BACK-TO-BACK ROUNDS")
                }
                preset.roundsConfigurable -> {
                    roundsStepper()
                    InfoStrip("ROUND ${formatClock(preset.roundSeconds)}  •  REST ${formatClock(preset.restSeconds)}  •  FIXED")
                }
            }

            TotalTimeCard(rounds = rounds, roundSeconds = roundSeconds, restSeconds = restSeconds)
        }

        PunchButton(
            text = "START FIGHT  →",
            onClick = { onStart(rounds, roundSeconds, restSeconds) },
            modifier = Modifier.fillMaxWidth().padding(end = 5.dp, bottom = 5.dp),
            minHeight = 68.dp,
            textStyle = CombatType.HeadlineXl
        )
    }
}

@Composable
private fun ConfigHeader(title: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .inkPanel(CombatColors.PanelNavyAlt, shadow = 3.dp)
                .clickable(role = Role.Button, onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Text("←", style = CombatType.HeadlineLg, color = CombatColors.Paper)
        }
        Column(Modifier.padding(start = 14.dp)) {
            Text(
                title,
                style = CombatType.HeadlineLg.copy(fontStyle = FontStyle.Italic),
                color = CombatColors.Paper,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("SET UP YOUR ROUNDS", style = CombatType.LabelSm, color = CombatColors.ImpactYellow)
        }
    }
}

@Composable
private fun StepperCard(
    title: String,
    subtitle: String,
    display: String,
    unit: String,
    plusColor: Color,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    tag: String? = null,
    tagColor: Color = CombatColors.ImpactYellow
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .inkPanel(CombatColors.PanelNavy)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(title, style = CombatType.HeadlineLg, color = CombatColors.Paper)
                Text(subtitle, style = CombatType.LabelSm, color = CombatColors.BurstBlueLight)
            }
            if (tag != null) TagBadge(tag, tagColor, CombatColors.Paper)
        }
        Row(
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(CombatColors.Night)
                .border(2.dp, CombatColors.Ink, RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoundStepButton("−", CombatColors.PanelNavyAlt, onMinus)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(display, style = CombatType.Clock.copy(fontSize = 54.sp), color = CombatColors.Paper, maxLines = 1)
                Text(unit, style = CombatType.LabelSm, color = CombatColors.Paper.copy(alpha = 0.55f))
            }
            RoundStepButton("+", plusColor, onPlus)
        }
    }
}

@Composable
private fun RoundStepButton(symbol: String, color: Color, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val shift = if (pressed) 2.dp else 0.dp
    Box(
        modifier = Modifier
            .size(64.dp)
            .offset(shift, shift)
            .inkPanel(color, shadow = 4.dp - shift, shape = CircleShape)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, style = CombatType.HeadlineXl, color = CombatColors.Paper)
    }
}

@Composable
private fun InfoStrip(text: String) {
    Text(
        text = text,
        style = CombatType.LabelLg,
        color = CombatColors.Paper.copy(alpha = 0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .inkPanel(CombatColors.PanelNavy, shadow = 3.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    )
}

@Composable
private fun TotalTimeCard(rounds: Int, roundSeconds: Int, restSeconds: Int) {
    val work = rounds * roundSeconds
    val rest = (rounds - 1).coerceAtLeast(0) * restSeconds
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .inkPanel(CombatColors.PanelNavy)
            .halftone(CombatColors.PanelNavyAlt, fade = true)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("TOTAL WORKOUT TIME", style = CombatType.LabelLg, color = CombatColors.Paper, modifier = Modifier.weight(1f))
            Text(formatClock(work + rest), style = CombatType.HeadlineXl, color = CombatColors.ImpactYellow)
        }
        Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            LegendDot(CombatColors.GloveRed)
            Text("Work ${formatClock(work)}", style = CombatType.BodyLg, color = CombatColors.Paper.copy(alpha = 0.8f))
            Text("   •   ", style = CombatType.BodyLg, color = CombatColors.Paper.copy(alpha = 0.4f))
            LegendDot(CombatColors.BurstBlueLight)
            Text("Rest ${formatClock(rest)}", style = CombatType.BodyLg, color = CombatColors.Paper.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        Modifier
            .padding(end = 6.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}
