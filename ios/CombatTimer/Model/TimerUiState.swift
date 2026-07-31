import Foundation

struct TimerUiState {
    var preset: SportPreset?
    var phase: TimerPhase = .idle
    var totalRounds: Int = 1
    var currentRound: Int = 1
    var roundSeconds: Int = 0
    var restSeconds: Int = 0
    /// Countdown remaining for round/rest; count-up elapsed for golden score.
    var secondsRemaining: Int = 0
    var isRunning: Bool = false
}
