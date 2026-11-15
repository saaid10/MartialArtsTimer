package com.saee.combattimer.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.TimerUiState
import com.saee.combattimer.TimerViewModel
import com.saee.combattimer.model.TimerPhase
import com.saee.combattimer.ui.components.ChamferShape
import com.saee.combattimer.ui.components.PhaseBanner
import com.saee.combattimer.ui.components.PunchButton
import com.saee.combattimer.ui.components.RoundCellState
import com.saee.combattimer.ui.components.RoundSequence
import com.saee.combattimer.ui.components.SpeedLineBurst
import com.saee.combattimer.ui.components.halftone
import com.saee.combattimer.ui.components.inkPanel
import com.saee.combattimer.ui.components.sunburst
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.ui.theme.CombatType

private const val FINAL_SECONDS_WARNING = 10

private data class PhaseStyle(val title: String, val color: Color, val content: Color)

private fun phaseStyle(phase: TimerPhase): PhaseStyle = when (phase) {
    TimerPhase.IDLE -> PhaseStyle("READY", CombatColors.PanelNavyAlt, CombatColors.Paper)
    TimerPhase.ROUND -> PhaseStyle("FIGHT!", CombatColors.GloveRed, CombatColors.Paper)
    TimerPhase.REST -> PhaseStyle("REST", CombatColors.BurstBlue, CombatColors.Paper)
    TimerPhase.AWAITING_GOLDEN_SCORE_DECISION -> PhaseStyle("TIME!", CombatColors.GloveRed, CombatColors.Paper)
    TimerPhase.GOLDEN_SCORE -> PhaseStyle("GOLDEN SCORE", CombatColors.ImpactYellow, CombatColors.Ink)
    TimerPhase.FINISHED -> PhaseStyle("FINISHED!", CombatColors.GloveRed, CombatColors.Paper)
}

private fun phaseCaption(state: TimerUiState): String = when (state.phase) {
    TimerPhase.IDLE -> "TAP START"
    TimerPhase.ROUND -> if (state.currentRound == state.totalRounds && state.totalRounds > 1) "FINAL ROUND" else ""
    TimerPhase.REST -> "NEXT: ROUND ${state.currentRound + 1}"
    TimerPhase.AWAITING_GOLDEN_SCORE_DECISION -> "REGULATION OVER"
    TimerPhase.GOLDEN_SCORE -> "FIRST SCORE WINS"
    TimerPhase.FINISHED -> ""
}

/** Zero-padded MM:SS — the big clock always shows 4 digits so it doesn't jump in width. */
private fun padClock(totalSeconds: Int): String = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

@Composable
fun TimerScreen(viewModel: TimerViewModel, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val preset = state.preset ?: return

    // Keep the screen awake while the clock is running so it stays readable mid-round.
    val view = LocalView.current
    DisposableEffect(state.isRunning) {
        view.keepScreenOn = state.isRunning
        onDispose { view.keepScreenOn = false }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CombatColors.Night)
            .padding(20.dp)
    ) {
        TopBar(state = state, onExit = onExit)
        Spacer(Modifier.height(16.dp))

        if (state.phase == TimerPhase.FINISHED) {
            FinishedContent(
                state = state,
                sportName = preset.displayName.uppercase(),
                hasGoldenScore = preset.hasGoldenScore,
                onRestart = viewModel::reset,
                onNewFight = onExit
            )
            return@Column
        }

        val style = phaseStyle(state.phase)
        val bannerColor by animateColorAsState(style.color, tween(400), label = "banner")
        PhaseBanner(title = style.title, caption = phaseCaption(state), color = bannerColor, contentColor = style.content)
        Spacer(Modifier.height(16.dp))

        ClockPanel(state = state, modifier = Modifier.weight(1f).fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        RoundSequenceCard(state)
        Spacer(Modifier.height(18.dp))

        if (state.phase == TimerPhase.AWAITING_GOLDEN_SCORE_DECISION) {
            GoldenScoreDecision(onGoldenScore = viewModel::startGoldenScore, onDecided = viewModel::finishMatch)
        } else {
            RunningControls(
                state = state,
                onToggle = {
                    when {
                        state.isRunning -> viewModel.pause()
                        state.phase == TimerPhase.IDLE -> viewModel.start()
                        else -> viewModel.resume()
                    }
                },
                onReset = viewModel::reset
            )
        }
    }
}

@Composable
private fun TopBar(state: TimerUiState, onExit: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .heightIn(min = 52.dp)
                .inkPanel(CombatColors.PanelNavyAlt, shadow = 3.dp)
                .clickable(role = Role.Button, onClick = onExit)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("✕  EXIT", style = CombatType.HeadlineMd, color = CombatColors.Paper)
        }
        Spacer(Modifier.weight(1f))
        val roundText = when (state.phase) {
            TimerPhase.GOLDEN_SCORE -> "SUDDEN DEATH"
            TimerPhase.FINISHED -> "FINAL BELL"
            else -> "ROUND ${state.currentRound} / ${state.totalRounds}"
        }
        Box(
            modifier = Modifier
                .heightIn(min = 52.dp)
                .inkPanel(CombatColors.PanelNavyAlt, shadow = 3.dp, shape = ChamferShape(10.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(roundText, style = CombatType.HeadlineMd.copy(fontStyle = FontStyle.Italic), color = CombatColors.ImpactYellow)
        }
    }
}

@Composable
private fun ClockPanel(state: TimerUiState, modifier: Modifier = Modifier) {
    val isGolden = state.phase == TimerPhase.GOLDEN_SCORE
    val isAwaiting = state.phase == TimerPhase.AWAITING_GOLDEN_SCORE_DECISION
    val finalSeconds = state.phase == TimerPhase.ROUND && state.secondsRemaining <= FINAL_SECONDS_WARNING

    // Last 10 seconds of a round: clock pulses red to match the clapper.
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    val clockColor = when {
        finalSeconds -> CombatColors.GloveRed.copy(alpha = if (state.isRunning) pulse else 1f)
        isGolden -> CombatColors.ImpactYellow
        else -> CombatColors.Paper
    }
    val clockText = when {
        isAwaiting -> padClock(0)
        isGolden -> "+" + padClock(state.secondsRemaining)
        else -> padClock(state.secondsRemaining)
    }
    val phaseTotal = when (state.phase) {
        TimerPhase.REST -> state.restSeconds
        TimerPhase.ROUND, TimerPhase.IDLE -> state.roundSeconds
        else -> 0
    }
    val progress = if (phaseTotal > 0) state.secondsRemaining.toFloat() / phaseTotal else 0f
    val accent = phaseStyle(state.phase).color.takeIf { state.phase != TimerPhase.IDLE } ?: CombatColors.ImpactYellow

    Box(
        modifier = modifier
            .inkPanel(CombatColors.PanelNavy, shadow = 5.dp, border = 3.dp)
            .halftone(CombatColors.PanelNavyAlt, fade = true),
        contentAlignment = Alignment.Center
    ) {
        if (finalSeconds || isGolden) {
            SpeedLineBurst(Modifier.fillMaxSize(), lineColor = clockColor, baseAlpha = 0.18f)
        }
        if (state.phase == TimerPhase.REST) {
            RestRing(progress = progress, color = CombatColors.BurstBlueLight)
        }

        Text(
            text = if (isGolden) "ELAPSED" else "REMAINING",
            style = CombatType.LabelLg,
            color = CombatColors.ImpactYellow,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            // Smaller inside the rest ring so the digits clear the arc.
            val factor = if (state.phase == TimerPhase.REST) 0.24f else 0.34f
            val size = (maxWidth.value * factor).coerceAtMost(150f)
            Text(
                text = clockText,
                style = CombatType.Clock.copy(fontSize = size.sp),
                color = clockColor,
                maxLines = 1
            )
        }

        if (phaseTotal > 0) {
            ProgressBar(
                progress = progress,
                color = accent,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RestRing(progress: Float, color: Color) {
    BoxWithConstraints(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        val diameter = minOf(maxWidth, maxHeight)
        Canvas(Modifier.size(diameter)) {
            val stroke = 10.dp.toPx()
            val inset = stroke / 2
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(color.copy(alpha = 0.15f), 0f, 360f, false, Offset(inset, inset), arcSize, style = Stroke(stroke))
            drawArc(color, -90f, 360f * progress, false, Offset(inset, inset), arcSize, style = Stroke(stroke, cap = StrokeCap.Square))
        }
    }
}

@Composable
private fun ProgressBar(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(6.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(3.dp))
            .background(CombatColors.PanelNavyAlt)
    ) {
        Box(
            Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(6.dp)
                .background(color)
        )
    }
}

@Composable
private fun RoundSequenceCard(state: TimerUiState) {
    val stateOf: (Int) -> RoundCellState = { round ->
        when (state.phase) {
            TimerPhase.IDLE, TimerPhase.ROUND -> when {
                round < state.currentRound -> RoundCellState.DONE
                round == state.currentRound -> RoundCellState.ACTIVE
                else -> RoundCellState.UPCOMING
            }
            TimerPhase.REST -> if (round <= state.currentRound) RoundCellState.DONE else RoundCellState.UPCOMING
            else -> RoundCellState.DONE
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .inkPanel(CombatColors.PanelNavy)
            .padding(14.dp)
    ) {
        Row(Modifier.padding(bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("ROUND SEQUENCE", style = CombatType.LabelLg, color = CombatColors.Paper, modifier = Modifier.weight(1f))
            Text("${state.currentRound} OF ${state.totalRounds}", style = CombatType.LabelLg, color = CombatColors.ImpactYellow)
        }
        RoundSequence(totalRounds = state.totalRounds, stateOf = stateOf)
    }
}

@Composable
private fun RunningControls(state: TimerUiState, onToggle: () -> Unit, onReset: () -> Unit) {
    val (label, color, content) = when {
        state.isRunning -> Triple("PAUSE", CombatColors.ImpactYellow, CombatColors.Ink)
        state.phase == TimerPhase.IDLE -> Triple("▶  START", CombatColors.GloveRed, CombatColors.Paper)
        else -> Triple("▶  RESUME", CombatColors.GloveRed, CombatColors.Paper)
    }
    Row(
        Modifier.fillMaxWidth().padding(end = 5.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PunchButton(
            text = label,
            onClick = onToggle,
            container = color,
            contentColor = content,
            minHeight = 76.dp,
            textStyle = CombatType.HeadlineXl,
            modifier = Modifier.weight(1.7f)
        )
        PunchButton(
            text = "↺ RESET",
            onClick = onReset,
            container = CombatColors.PanelNavyAlt,
            minHeight = 76.dp,
            textStyle = CombatType.HeadlineMd,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GoldenScoreDecision(onGoldenScore: () -> Unit, onDecided: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(end = 5.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        PunchButton(
            text = "START GOLDEN SCORE",
            subtitle = "SCORES LEVEL • FIRST SCORE WINS",
            onClick = onGoldenScore,
            container = CombatColors.ImpactYellow,
            contentColor = CombatColors.Ink,
            minHeight = 76.dp,
            modifier = Modifier.fillMaxWidth()
        )
        PunchButton(
            text = "MATCH DECIDED",
            subtitle = "WINNER IN REGULATION",
            onClick = onDecided,
            container = CombatColors.PanelNavyAlt,
            minHeight = 68.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FinishedContent(
    state: TimerUiState,
    sportName: String,
    hasGoldenScore: Boolean,
    onRestart: () -> Unit,
    onNewFight: () -> Unit
) {
    val regulationSeconds = state.totalRounds * state.roundSeconds + (state.totalRounds - 1).coerceAtLeast(0) * state.restSeconds

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(end = 5.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Impact hero
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .inkPanel(CombatColors.PanelNavy, shadow = 5.dp, border = 3.dp)
                .sunburst(CombatColors.ImpactYellow.copy(alpha = 0.16f))
                .halftone(CombatColors.PanelNavyAlt),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    Modifier
                        .inkPanel(CombatColors.GloveRed, shadow = 6.dp, border = 3.dp, shape = ChamferShape(12.dp))
                        .padding(horizontal = 28.dp, vertical = 6.dp)
                ) {
                    Text("FINISHED!", style = CombatType.HeadlineXl.copy(fontSize = 52.sp, fontStyle = FontStyle.Italic), color = CombatColors.Paper)
                }
                Text(sportName, style = CombatType.LabelLg, color = CombatColors.Paper.copy(alpha = 0.8f), modifier = Modifier.padding(top = 14.dp))
            }
        }

        // Summary
        Column(
            Modifier
                .fillMaxWidth()
                .inkPanel(CombatColors.PanelNavy)
                .padding(18.dp)
        ) {
            Text("BOUT SUMMARY", style = CombatType.LabelSm, color = CombatColors.BurstBlueLight)
            Text(
                "${state.totalRounds} ROUND${if (state.totalRounds == 1) "" else "S"} COMPLETE",
                style = CombatType.HeadlineLg,
                color = CombatColors.Paper
            )
            Column(
                Modifier
                    .padding(top = 14.dp)
                    .fillMaxWidth()
                    .inkPanel(CombatColors.Night, shadow = 0.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (hasGoldenScore) "REGULATION TIME" else "TOTAL TIME",
                    style = CombatType.LabelLg,
                    color = CombatColors.Paper.copy(alpha = 0.7f)
                )
                Text(padClock(regulationSeconds), style = CombatType.Clock.copy(fontSize = 72.sp), color = CombatColors.Paper)
                RoundSequence(totalRounds = state.totalRounds, stateOf = { RoundCellState.DONE }, modifier = Modifier.padding(top = 8.dp))
            }
        }

        PunchButton(
            text = "↺ RESTART FIGHT",
            onClick = onRestart,
            container = CombatColors.GloveRed,
            minHeight = 68.dp,
            modifier = Modifier.fillMaxWidth()
        )
        PunchButton(
            text = "NEW FIGHT",
            onClick = onNewFight,
            container = CombatColors.ImpactYellow,
            contentColor = CombatColors.Ink,
            minHeight = 68.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
