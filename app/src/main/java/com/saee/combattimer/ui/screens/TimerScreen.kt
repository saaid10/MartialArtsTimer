package com.saee.combattimer.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.TimerViewModel
import com.saee.combattimer.model.TimerPhase
import com.saee.combattimer.ui.components.ComicText
import com.saee.combattimer.ui.components.SevenSegmentDisplay
import com.saee.combattimer.ui.components.SpeedLineBurst
import com.saee.combattimer.ui.components.inkBorder
import com.saee.combattimer.ui.theme.CombatColors
import com.saee.combattimer.util.formatClock

private data class PhaseStyle(val label: String, val accent: Color, val dim: Color)

private fun phaseStyle(phase: TimerPhase): PhaseStyle = when (phase) {
    TimerPhase.IDLE -> PhaseStyle("READY", CombatColors.Bone, CombatColors.PanelBlack)
    TimerPhase.ROUND -> PhaseStyle("ROUND", CombatColors.CornerRed, CombatColors.CornerRedDim)
    TimerPhase.REST -> PhaseStyle("REST", CombatColors.CornerGreen, CombatColors.CornerGreenDim)
    TimerPhase.AWAITING_GOLDEN_SCORE_DECISION -> PhaseStyle("REGULATION OVER", CombatColors.CornerAmber, CombatColors.CornerAmberDim)
    TimerPhase.GOLDEN_SCORE -> PhaseStyle("GOLDEN SCORE", CombatColors.CornerAmber, CombatColors.CornerAmberDim)
    TimerPhase.FINISHED -> PhaseStyle("FINISHED", CombatColors.Bone, CombatColors.PanelBlack)
}

@Composable
fun TimerScreen(viewModel: TimerViewModel, onExit: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val preset = state.preset ?: return
    val style = phaseStyle(state.phase)

    // Keep the screen awake while the round/rest/golden-score clock is actively
    // running, so it stays visible without needing a touch every few seconds.
    val view = LocalView.current
    DisposableEffect(state.isRunning) {
        view.keepScreenOn = state.isRunning
        onDispose { view.keepScreenOn = false }
    }

    val backdropTint by animateColorAsState(targetValue = style.dim, animationSpec = tween(500), label = "backdrop")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(backdropTint.copy(alpha = 0.55f), CombatColors.Night, CombatColors.Night)
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onExit) {
                    Text("EXIT", color = CombatColors.Bone.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Text(
                    text = preset.displayName.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = CombatColors.Bone.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(28.dp))

            PhaseBadge(style)

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (state.phase == TimerPhase.GOLDEN_SCORE) "SUDDEN DEATH" else "ROUND ${state.currentRound} OF ${state.totalRounds}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = CombatColors.Bone.copy(alpha = 0.55f)
            )

            Spacer(Modifier.height(36.dp))

            DisplayPanel(state.phase, style, clockLabel(state))

            Spacer(Modifier.height(44.dp))

            when (state.phase) {
                TimerPhase.AWAITING_GOLDEN_SCORE_DECISION -> GoldenScorePrompt(
                    onDecided = viewModel::finishMatch,
                    onGoldenScore = viewModel::startGoldenScore
                )

                TimerPhase.FINISHED -> {
                    ComicText(
                        text = "MATCH FINISHED",
                        fontSize = 22.sp,
                        color = CombatColors.Paper,
                        outlineWidth = 6f,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    CombatOutlinedButton(text = "RESTART", onClick = viewModel::reset)
                }

                else -> RunningControls(
                    isRunning = state.isRunning,
                    phase = state.phase,
                    onToggle = {
                        if (state.isRunning) viewModel.pause()
                        else if (state.phase == TimerPhase.IDLE) viewModel.start()
                        else viewModel.resume()
                    },
                    onReset = viewModel::reset
                )
            }
        }
    }
}

private fun clockLabel(state: com.saee.combattimer.TimerUiState): String {
    val clock = formatClock(state.secondsRemaining)
    return if (state.phase == TimerPhase.GOLDEN_SCORE) "+$clock" else clock
}

@Composable
private fun DisplayPanel(phase: TimerPhase, style: PhaseStyle, clockLabel: String) {
    val glow by animateColorAsState(targetValue = style.accent, animationSpec = tween(400), label = "glow")
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        SpeedLineBurst(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            lineColor = glow,
            baseAlpha = 0.4f
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CombatColors.PanelNavy)
                .border(inkBorder(3.5.dp), RoundedCornerShape(14.dp))
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            val plusPrefix = clockLabel.startsWith("+")
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (plusPrefix) {
                    Text(
                        text = "+",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = glow,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
                SevenSegmentDisplay(
                    value = clockLabel.removePrefix("+"),
                    digitHeight = 84.dp,
                    onColor = glow
                )
            }
        }
    }
}

@Composable
private fun PhaseBadge(style: PhaseStyle) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(CombatColors.PanelNavy)
            .border(inkBorder(3.dp), RoundedCornerShape(50))
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        ComicText(
            text = style.label,
            fontSize = 20.sp,
            color = style.accent,
            outlineWidth = 5f,
            letterSpacing = 2.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RunningControls(
    isRunning: Boolean,
    phase: TimerPhase,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(
            onClick = onToggle,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CombatColors.GloveRed,
                contentColor = Color.White
            ),
            border = inkBorder(),
            modifier = Modifier.height(56.dp)
        ) {
            Text(
                text = if (isRunning) "PAUSE" else if (phase == TimerPhase.IDLE) "START" else "RESUME",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
        CombatOutlinedButton(text = "RESET", onClick = onReset)
    }
}

@Composable
private fun GoldenScorePrompt(onDecided: () -> Unit, onGoldenScore: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "IF SCORES ARE LEVEL, START GOLDEN SCORE.",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
            color = CombatColors.Bone.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CombatOutlinedButton(text = "MATCH DECIDED", onClick = onDecided)
            Button(
                onClick = onGoldenScore,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CombatColors.ImpactYellow, contentColor = Color.Black),
                border = inkBorder(),
                modifier = Modifier.height(56.dp)
            ) {
                Text("GOLDEN SCORE", fontSize = 15.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
private fun CombatOutlinedButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = CombatColors.Paper),
        border = inkBorder(),
        modifier = Modifier.height(56.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
    }
}
