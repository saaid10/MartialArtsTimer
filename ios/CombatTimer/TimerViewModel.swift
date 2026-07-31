import Foundation

@MainActor
final class TimerViewModel: ObservableObject {
    @Published private(set) var uiState = TimerUiState()

    private let soundEngine = SoundEngine()
    private var tickTask: Task<Void, Never>?

    private static let tenSecondWarning = 10
    private static let restCountdownBeeps = 5
    private static let resumablePhases: Set<TimerPhase> = [.round, .rest, .goldenScore]

    /// Loads a sport, optionally overriding its configurable fields, ready to start.
    func configure(preset: SportPreset, rounds: Int? = nil, roundSeconds: Int? = nil, restSeconds: Int? = nil) {
        tickTask?.cancel()
        let resolvedRoundSeconds = roundSeconds ?? preset.roundSeconds
        uiState = TimerUiState(
            preset: preset,
            phase: .idle,
            totalRounds: rounds ?? preset.defaultRounds,
            currentRound: 1,
            roundSeconds: resolvedRoundSeconds,
            restSeconds: restSeconds ?? preset.restSeconds,
            secondsRemaining: resolvedRoundSeconds,
            isRunning: false
        )
    }

    func start() {
        if uiState.phase == .idle {
            uiState.phase = .round
            uiState.secondsRemaining = uiState.roundSeconds
        }
        runTicker()
    }

    func pause() {
        tickTask?.cancel()
        uiState.isRunning = false
    }

    func resume() {
        if Self.resumablePhases.contains(uiState.phase) {
            runTicker()
        }
    }

    func reset() {
        guard let preset = uiState.preset else { return }
        configure(
            preset: preset,
            rounds: uiState.totalRounds,
            roundSeconds: uiState.roundSeconds,
            restSeconds: uiState.restSeconds
        )
    }

    /// Called from the Judo "match tied" prompt after the final round buzzer.
    func startGoldenScore() {
        tickTask?.cancel()
        uiState.phase = .goldenScore
        uiState.secondsRemaining = 0
        uiState.isRunning = true
        runTicker()
    }

    /// Ends the match without golden score (decision reached in regulation, or golden score scored).
    func finishMatch() {
        tickTask?.cancel()
        uiState.phase = .finished
        uiState.isRunning = false
    }

    private func runTicker() {
        tickTask?.cancel()
        uiState.isRunning = true
        tickTask = Task { [weak self] in
            while true {
                try? await Task.sleep(nanoseconds: 1_000_000_000)
                if Task.isCancelled { return }
                await self?.tick()
            }
        }
    }

    private func tick() {
        switch uiState.phase {
        case .round: tickRound()
        case .rest: tickRest()
        case .goldenScore: uiState.secondsRemaining += 1
        default: break
        }
    }

    private func tickRound() {
        let remaining = uiState.secondsRemaining - 1
        if remaining == Self.tenSecondWarning {
            soundEngine.playTenSecondClapper()
            uiState.secondsRemaining = remaining
        } else if remaining <= 0 {
            soundEngine.playRoundEndHorn()
            advancePastRoundEnd()
        } else {
            uiState.secondsRemaining = remaining
        }
    }

    private func tickRest() {
        let remaining = uiState.secondsRemaining - 1
        if remaining >= 1 && remaining <= Self.restCountdownBeeps {
            soundEngine.playCountdownBeep()
            uiState.secondsRemaining = remaining
        } else if remaining <= 0 {
            soundEngine.playFinalCountdownBeep()
            uiState.phase = .round
            uiState.currentRound += 1
            uiState.secondsRemaining = uiState.roundSeconds
        } else {
            uiState.secondsRemaining = remaining
        }
    }

    private func advancePastRoundEnd() {
        if uiState.currentRound < uiState.totalRounds {
            uiState.phase = .rest
            uiState.secondsRemaining = uiState.restSeconds
        } else if uiState.preset?.hasGoldenScore == true {
            tickTask?.cancel()
            uiState.phase = .awaitingGoldenScoreDecision
            uiState.isRunning = false
        } else {
            tickTask?.cancel()
            uiState.phase = .finished
            uiState.isRunning = false
        }
    }

    deinit {
        tickTask?.cancel()
    }
}
