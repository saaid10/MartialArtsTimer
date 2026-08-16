package com.saee.combattimer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.ui.components.ComicText
import com.saee.combattimer.ui.components.RingRopeDivider
import com.saee.combattimer.ui.components.inkBorder
import com.saee.combattimer.ui.theme.CombatColors
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CombatColors.Night)
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("< BACK", color = CombatColors.Paper.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        ComicText(
            text = preset.displayName.uppercase(),
            fontSize = 27.sp,
            color = CombatColors.ImpactYellow,
            outlineWidth = 7f,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
        )
        RingRopeDivider(modifier = Modifier.padding(bottom = 24.dp))

        if (preset.roundsConfigurable && !preset.roundSecondsConfigurable) {
            IntStepperRow(
                label = "ROUNDS",
                value = rounds,
                onChange = { rounds = it.coerceIn(preset.roundsRange.first, preset.roundsRange.last) },
                step = 1,
                display = rounds.toString()
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "ROUND ${formatClock(preset.roundSeconds)}  ·  REST ${formatClock(preset.restSeconds)}  ·  FIXED",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = CombatColors.Bone.copy(alpha = 0.5f)
            )
        }

        if (preset.roundSecondsConfigurable) {
            IntStepperRow(
                label = "ROUND DURATION",
                value = roundSeconds,
                onChange = { roundSeconds = it.coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS) },
                step = DURATION_STEP,
                display = formatClock(roundSeconds)
            )
            Spacer(Modifier.height(20.dp))
            IntStepperRow(
                label = "ROUNDS",
                value = rounds,
                onChange = { rounds = it.coerceIn(preset.roundsRange.first, preset.roundsRange.last) },
                step = 1,
                display = rounds.toString()
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = "NO REST · BACK-TO-BACK ROUNDS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = CombatColors.Bone.copy(alpha = 0.5f)
            )
        }

        if (preset.fullyConfigurable) {
            IntStepperRow(
                label = "ROUND DURATION",
                value = roundSeconds,
                onChange = { roundSeconds = it.coerceIn(MIN_DURATION_SECONDS, MAX_DURATION_SECONDS) },
                step = DURATION_STEP,
                display = formatClock(roundSeconds)
            )
            Spacer(Modifier.height(20.dp))
            IntStepperRow(
                label = "REST DURATION",
                value = restSeconds,
                onChange = { restSeconds = it.coerceIn(0, MAX_DURATION_SECONDS) },
                step = DURATION_STEP,
                display = formatClock(restSeconds)
            )
            Spacer(Modifier.height(20.dp))
            IntStepperRow(
                label = "ROUNDS",
                value = rounds,
                onChange = { rounds = it.coerceIn(1, 30) },
                step = 1,
                display = rounds.toString()
            )
        }

        Spacer(Modifier.height(36.dp))

        Button(
            onClick = { onStart(rounds, roundSeconds, restSeconds) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CombatColors.CornerRed, contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("START", fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun IntStepperRow(
    label: String,
    value: Int,
    step: Int,
    display: String,
    onChange: (Int) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = CombatColors.Bone.copy(alpha = 0.55f)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepperButton(symbol = "-", onClick = { onChange(value - step) })
            Text(text = display, fontSize = 26.sp, fontWeight = FontWeight.Black, color = CombatColors.Bone)
            StepperButton(symbol = "+", onClick = { onChange(value + step) })
        }
    }
}

@Composable
private fun StepperButton(symbol: String, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier.size(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, CombatColors.RopeGrey),
        colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = CombatColors.CornerRed)
    ) {
        Text(symbol, fontSize = 24.sp, fontWeight = FontWeight.Black)
    }
}
