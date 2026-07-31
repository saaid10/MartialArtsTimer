import SwiftUI

private struct PhaseStyle {
    let label: String
    let accent: Color
    let dim: Color
}

private func phaseStyle(_ phase: TimerPhase) -> PhaseStyle {
    switch phase {
    case .idle:
        return PhaseStyle(label: "READY", accent: CombatColors.paper, dim: CombatColors.panelNavy)
    case .round:
        return PhaseStyle(label: "ROUND", accent: CombatColors.gloveRed, dim: CombatColors.gloveRedDark)
    case .rest:
        return PhaseStyle(label: "REST", accent: CombatColors.burstBlue, dim: CombatColors.burstBlueDark)
    case .awaitingGoldenScoreDecision:
        return PhaseStyle(label: "REGULATION OVER", accent: CombatColors.impactYellow, dim: CombatColors.impactYellowDark)
    case .goldenScore:
        return PhaseStyle(label: "GOLDEN SCORE", accent: CombatColors.impactYellow, dim: CombatColors.impactYellowDark)
    case .finished:
        return PhaseStyle(label: "FINISHED", accent: CombatColors.paper, dim: CombatColors.panelNavy)
    }
}

private func clockLabel(_ state: TimerUiState) -> String {
    let clock = formatClock(state.secondsRemaining)
    return state.phase == .goldenScore ? "+\(clock)" : clock
}

struct TimerScreen: View {
    @ObservedObject var viewModel: TimerViewModel
    let onExit: () -> Void

    var body: some View {
        if let preset = viewModel.uiState.preset {
            content(preset: preset)
        } else {
            EmptyView()
        }
    }

    @ViewBuilder
    private func content(preset: SportPreset) -> some View {
        let state = viewModel.uiState
        let style = phaseStyle(state.phase)

        ZStack {
            LinearGradient(
                colors: [style.dim.opacity(0.55), CombatColors.night, CombatColors.night],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack {
                HStack {
                    Button(action: onExit) {
                        Text("EXIT")
                            .font(.system(size: 15, weight: .bold))
                            .tracking(1)
                            .foregroundColor(CombatColors.paper.opacity(0.8))
                    }
                    Spacer()
                    Text(preset.displayName.uppercased())
                        .font(.system(size: 14, weight: .black))
                        .tracking(1)
                        .foregroundColor(CombatColors.paper.opacity(0.6))
                }

                Spacer().frame(height: 28)

                PhaseBadge(style: style)

                Spacer().frame(height: 10)

                Text(state.phase == .goldenScore ? "SUDDEN DEATH" : "ROUND \(state.currentRound) OF \(state.totalRounds)")
                    .font(.system(size: 15, weight: .bold))
                    .tracking(1.5)
                    .foregroundColor(CombatColors.paper.opacity(0.55))

                Spacer().frame(height: 36)

                DisplayPanel(style: style, clockLabel: clockLabel(state))

                Spacer().frame(height: 44)

                switch state.phase {
                case .awaitingGoldenScoreDecision:
                    GoldenScorePrompt(
                        onDecided: { viewModel.finishMatch() },
                        onGoldenScore: { viewModel.startGoldenScore() }
                    )
                case .finished:
                    VStack(spacing: 18) {
                        ComicText(text: "MATCH FINISHED", fontSize: 22, color: CombatColors.paper, outlineWidth: 1.3, tracking: 1)
                        CombatOutlinedButton(text: "RESTART") { viewModel.reset() }
                    }
                default:
                    RunningControls(
                        isRunning: state.isRunning,
                        phase: state.phase,
                        onToggle: {
                            if state.isRunning {
                                viewModel.pause()
                            } else if state.phase == .idle {
                                viewModel.start()
                            } else {
                                viewModel.resume()
                            }
                        },
                        onReset: { viewModel.reset() }
                    )
                }

                Spacer()
            }
            .padding(20)
        }
        .onChange(of: state.isRunning) { isRunning in
            UIApplication.shared.isIdleTimerDisabled = isRunning
        }
        .onDisappear {
            UIApplication.shared.isIdleTimerDisabled = false
        }
    }
}

private struct DisplayPanel: View {
    let style: PhaseStyle
    let clockLabel: String

    var body: some View {
        let plusPrefix = clockLabel.hasPrefix("+")
        let digits = plusPrefix ? String(clockLabel.dropFirst()) : clockLabel

        ZStack {
            SpeedLineBurst(lineColor: style.accent, baseAlpha: 0.4)
                .frame(maxWidth: .infinity)
                .frame(height: 190)

            HStack(spacing: 0) {
                if plusPrefix {
                    Text("+")
                        .font(.system(size: 40, weight: .black))
                        .foregroundColor(style.accent)
                        .padding(.trailing, 4)
                }
                SevenSegmentDisplay(value: digits, digitHeight: 84, onColor: style.accent)
            }
            .padding(.vertical, 28)
            .frame(maxWidth: .infinity)
            .background(CombatColors.panelNavy)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(RoundedRectangle(cornerRadius: 14).stroke(CombatColors.ink, lineWidth: 3.5))
        }
    }
}

private struct PhaseBadge: View {
    let style: PhaseStyle

    var body: some View {
        ComicText(text: style.label, fontSize: 20, color: style.accent, outlineWidth: 1.1, tracking: 2)
            .padding(.horizontal, 20)
            .padding(.vertical, 8)
            .background(CombatColors.panelNavy)
            .clipShape(Capsule())
            .overlay(Capsule().stroke(CombatColors.ink, lineWidth: 3))
    }
}

private struct RunningControls: View {
    let isRunning: Bool
    let phase: TimerPhase
    let onToggle: () -> Void
    let onReset: () -> Void

    var body: some View {
        HStack(spacing: 16) {
            Button(action: onToggle) {
                Text(isRunning ? "PAUSE" : (phase == .idle ? "START" : "RESUME"))
                    .font(.system(size: 18, weight: .black))
                    .tracking(1)
                    .foregroundColor(.white)
                    .frame(height: 56)
                    .padding(.horizontal, 28)
                    .background(CombatColors.gloveRed)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(CombatColors.ink, lineWidth: 3))
            }
            .buttonStyle(.plain)

            CombatOutlinedButton(text: "RESET", onTap: onReset)
        }
    }
}

private struct GoldenScorePrompt: View {
    let onDecided: () -> Void
    let onGoldenScore: () -> Void

    var body: some View {
        VStack(spacing: 18) {
            Text("IF SCORES ARE LEVEL, START GOLDEN SCORE.")
                .font(.system(size: 13, weight: .bold))
                .tracking(0.5)
                .multilineTextAlignment(.center)
                .foregroundColor(CombatColors.paper.opacity(0.6))

            HStack(spacing: 16) {
                CombatOutlinedButton(text: "MATCH DECIDED", onTap: onDecided)
                Button(action: onGoldenScore) {
                    Text("GOLDEN SCORE")
                        .font(.system(size: 15, weight: .black))
                        .tracking(0.5)
                        .foregroundColor(.black)
                        .frame(height: 56)
                        .padding(.horizontal, 20)
                        .background(CombatColors.impactYellow)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(CombatColors.ink, lineWidth: 3))
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private struct CombatOutlinedButton: View {
    let text: String
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            Text(text)
                .font(.system(size: 16, weight: .black))
                .tracking(1)
                .foregroundColor(CombatColors.paper)
                .frame(height: 56)
                .padding(.horizontal, 20)
                .overlay(RoundedRectangle(cornerRadius: 14).stroke(CombatColors.ink, lineWidth: 3))
        }
        .buttonStyle(.plain)
    }
}
