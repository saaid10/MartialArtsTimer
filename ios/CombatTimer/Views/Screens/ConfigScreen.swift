import SwiftUI

private let minDurationSeconds = 5
private let maxDurationSeconds = 20 * 60
private let durationStep = 5

struct ConfigScreen: View {
    let preset: SportPreset
    let onBack: () -> Void
    let onStart: (_ rounds: Int, _ roundSeconds: Int, _ restSeconds: Int) -> Void

    @State private var rounds: Int
    @State private var roundSeconds: Int
    @State private var restSeconds: Int

    init(preset: SportPreset, onBack: @escaping () -> Void, onStart: @escaping (Int, Int, Int) -> Void) {
        self.preset = preset
        self.onBack = onBack
        self.onStart = onStart
        _rounds = State(initialValue: preset.defaultRounds)
        _roundSeconds = State(initialValue: preset.roundSeconds)
        _restSeconds = State(initialValue: preset.restSeconds)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Button(action: onBack) {
                Text("< BACK")
                    .font(.system(size: 15, weight: .bold))
                    .tracking(1)
                    .foregroundColor(CombatColors.paper.opacity(0.8))
            }

            ComicText(text: preset.displayName.uppercased(), fontSize: 27, color: CombatColors.impactYellow, outlineWidth: 1.5, tracking: 0.5)
                .padding(.top, 8)
                .padding(.bottom, 14)

            RingRopeDivider()
                .padding(.bottom, 24)

            if preset.roundsConfigurable {
                IntStepperRow(label: "ROUNDS", value: rounds, step: 1, display: "\(rounds)") { newValue in
                    rounds = min(max(newValue, preset.roundsRange.lowerBound), preset.roundsRange.upperBound)
                }
                Text("ROUND \(formatClock(preset.roundSeconds))  \u{00B7}  REST \(formatClock(preset.restSeconds))  \u{00B7}  FIXED")
                    .font(.system(size: 13, weight: .bold))
                    .tracking(0.5)
                    .foregroundColor(CombatColors.paper.opacity(0.5))
                    .padding(.top, 14)
            }

            if preset.fullyConfigurable {
                IntStepperRow(label: "ROUND DURATION", value: roundSeconds, step: durationStep, display: formatClock(roundSeconds)) { newValue in
                    roundSeconds = min(max(newValue, minDurationSeconds), maxDurationSeconds)
                }
                .padding(.top, 20)

                IntStepperRow(label: "REST DURATION", value: restSeconds, step: durationStep, display: formatClock(restSeconds)) { newValue in
                    restSeconds = min(max(newValue, 0), maxDurationSeconds)
                }
                .padding(.top, 20)

                IntStepperRow(label: "ROUNDS", value: rounds, step: 1, display: "\(rounds)") { newValue in
                    rounds = min(max(newValue, 1), 30)
                }
                .padding(.top, 20)
            }

            Spacer(minLength: 36)

            Button(action: { onStart(rounds, roundSeconds, restSeconds) }) {
                Text("START")
                    .font(.system(size: 18, weight: .black))
                    .tracking(1)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(CombatColors.gloveRed)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(RoundedRectangle(cornerRadius: 14).stroke(CombatColors.ink, lineWidth: 3))
            }
            .buttonStyle(.plain)
        }
        .padding(20)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(CombatColors.night.ignoresSafeArea())
    }
}

private struct IntStepperRow: View {
    let label: String
    let value: Int
    let step: Int
    let display: String
    let onChange: (Int) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.system(size: 13, weight: .bold))
                .tracking(1)
                .foregroundColor(CombatColors.paper.opacity(0.55))

            HStack {
                StepperButton(symbol: "-") { onChange(value - step) }
                Spacer()
                Text(display)
                    .font(.system(size: 26, weight: .black))
                    .foregroundColor(CombatColors.paper)
                Spacer()
                StepperButton(symbol: "+") { onChange(value + step) }
            }
        }
    }
}

private struct StepperButton: View {
    let symbol: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(symbol)
                .font(.system(size: 24, weight: .black))
                .foregroundColor(CombatColors.gloveRed)
                .frame(width: 52, height: 52)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(CombatColors.ink, lineWidth: 2))
        }
        .buttonStyle(.plain)
    }
}
