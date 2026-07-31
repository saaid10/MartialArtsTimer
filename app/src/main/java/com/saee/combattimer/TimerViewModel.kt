package com.saee.combattimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.saee.combattimer.engine.SoundEngine
import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.model.TimerPhase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {

    private val soundEngine = SoundEngine(application)
    private var tickJob: Job? = null

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    /** Loads a sport, optionally overriding its configurable fields, ready to start. */
    fun configure(preset: SportPreset, rounds: Int? = null, roundSeconds: Int? = null, restSeconds: Int? = null) {
        tickJob?.cancel()
        _uiState.value = TimerUiState(
            preset = preset,
            phase = TimerPhase.IDLE,
            totalRounds = rounds ?: preset.defaultRounds,
            currentRound = 1,
            roundSeconds = roundSeconds ?: preset.roundSeconds,
            restSeconds = restSeconds ?: preset.restSeconds,
            secondsRemaining = roundSeconds ?: preset.roundSeconds,
            isRunning = false
        )
    }

    fun start() {
        val current = _uiState.value
        if (current.phase == TimerPhase.IDLE) {
            _uiState.update { it.copy(phase = TimerPhase.ROUND, secondsRemaining = it.roundSeconds) }
        }
        runTicker()
    }

    fun pause() {
        tickJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resume() {
        if (_uiState.value.phase in RESUMABLE_PHASES) {
            runTicker()
        }
    }

    fun reset() {
        val preset = _uiState.value.preset ?: return
        val rounds = _uiState.value.totalRounds
        val roundSeconds = _uiState.value.roundSeconds
        val restSeconds = _uiState.value.restSeconds
        configure(preset, rounds, roundSeconds, restSeconds)
    }

    /** Called from the Judo "match tied" prompt after the final round buzzer. */
    fun startGoldenScore() {
        tickJob?.cancel()
        _uiState.update { it.copy(phase = TimerPhase.GOLDEN_SCORE, secondsRemaining = 0, isRunning = true) }
        runTicker()
    }

    /** Ends the match without golden score (decision reached in regulation, or golden score scored). */
    fun finishMatch() {
        tickJob?.cancel()
        _uiState.update { it.copy(phase = TimerPhase.FINISHED, isRunning = false) }
    }

    private fun runTicker() {
        tickJob?.cancel()
        _uiState.update { it.copy(isRunning = true) }
        tickJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                tick()
            }
        }
    }

    private fun tick() {
        when (_uiState.value.phase) {
            TimerPhase.ROUND -> tickRound()
            TimerPhase.REST -> tickRest()
            TimerPhase.GOLDEN_SCORE -> _uiState.update { it.copy(secondsRemaining = it.secondsRemaining + 1) }
            else -> Unit
        }
    }

    private fun tickRound() {
        val remaining = _uiState.value.secondsRemaining - 1
        when {
            remaining == TEN_SECOND_WARNING -> {
                soundEngine.playTenSecondClapper()
                _uiState.update { it.copy(secondsRemaining = remaining) }
            }
            remaining <= 0 -> {
                soundEngine.playRoundEndHorn()
                advancePastRoundEnd()
            }
            else -> _uiState.update { it.copy(secondsRemaining = remaining) }
        }
    }

    private fun tickRest() {
        val remaining = _uiState.value.secondsRemaining - 1
        when {
            remaining in 1..REST_COUNTDOWN_BEEPS -> {
                soundEngine.playCountdownBeep()
                _uiState.update { it.copy(secondsRemaining = remaining) }
            }
            remaining <= 0 -> {
                soundEngine.playFinalCountdownBeep()
                _uiState.update {
                    it.copy(
                        phase = TimerPhase.ROUND,
                        currentRound = it.currentRound + 1,
                        secondsRemaining = it.roundSeconds
                    )
                }
            }
            else -> _uiState.update { it.copy(secondsRemaining = remaining) }
        }
    }

    private fun advancePastRoundEnd() {
        val state = _uiState.value
        when {
            state.currentRound < state.totalRounds -> {
                _uiState.update { it.copy(phase = TimerPhase.REST, secondsRemaining = it.restSeconds) }
            }
            state.preset?.hasGoldenScore == true -> {
                tickJob?.cancel()
                _uiState.update { it.copy(phase = TimerPhase.AWAITING_GOLDEN_SCORE_DECISION, isRunning = false) }
            }
            else -> {
                tickJob?.cancel()
                _uiState.update { it.copy(phase = TimerPhase.FINISHED, isRunning = false) }
            }
        }
    }

    override fun onCleared() {
        tickJob?.cancel()
        soundEngine.release()
    }

    private companion object {
        const val TEN_SECOND_WARNING = 10
        const val REST_COUNTDOWN_BEEPS = 5
        val RESUMABLE_PHASES = setOf(TimerPhase.ROUND, TimerPhase.REST, TimerPhase.GOLDEN_SCORE)
    }
}
