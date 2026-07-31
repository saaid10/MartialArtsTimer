package com.saee.combattimer

import com.saee.combattimer.model.SportPreset
import com.saee.combattimer.model.TimerPhase

data class TimerUiState(
    val preset: SportPreset? = null,
    val phase: TimerPhase = TimerPhase.IDLE,
    val totalRounds: Int = 1,
    val currentRound: Int = 1,
    val roundSeconds: Int = 0,
    val restSeconds: Int = 0,
    /** Countdown remaining for ROUND/REST; count-up elapsed for GOLDEN_SCORE. */
    val secondsRemaining: Int = 0,
    val isRunning: Boolean = false
)
