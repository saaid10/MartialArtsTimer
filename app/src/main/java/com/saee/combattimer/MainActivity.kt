package com.saee.combattimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.model.SportPresets
import com.saee.combattimer.ui.screens.ConfigScreen
import com.saee.combattimer.ui.screens.SportSelectionScreen
import com.saee.combattimer.ui.screens.TimerScreen
import com.saee.combattimer.ui.theme.CombatTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CombatTimerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CombatTimerApp()
                }
            }
        }
    }
}

private sealed class Screen {
    data object Selection : Screen()
    data class Config(val preset: SportPreset) : Screen()
    data object Timer : Screen()
}

@Composable
private fun CombatTimerApp(viewModel: TimerViewModel = viewModel()) {
    var screen by remember { mutableStateOf<Screen>(Screen.Selection) }

    when (val current = screen) {
        is Screen.Selection -> SportSelectionScreen(
            presets = SportPresets.ALL,
            onPresetSelected = { preset ->
                if (preset.isConfigurable) {
                    screen = Screen.Config(preset)
                } else {
                    viewModel.configure(preset)
                    screen = Screen.Timer
                }
            }
        )

        is Screen.Config -> ConfigScreen(
            preset = current.preset,
            onBack = { screen = Screen.Selection },
            onStart = { rounds, roundSeconds, restSeconds ->
                viewModel.configure(current.preset, rounds, roundSeconds, restSeconds)
                screen = Screen.Timer
            }
        )

        is Screen.Timer -> TimerScreen(
            viewModel = viewModel,
            onExit = { screen = Screen.Selection }
        )
    }
}
