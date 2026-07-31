import SwiftUI

struct SportSelectionScreen: View {
    let presets: [SportPreset]
    let onPresetSelected: (SportPreset) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            ComicText(text: "COMBAT TIMER", fontSize: 32, color: CombatColors.impactYellow, outlineWidth: 1.6, tracking: 0.5)
                .padding(.top, 2)
                .padding(.bottom, 6)

            Text("Pick a discipline. Fixed rules start immediately — Interval Setting and Boxing Pro let you configure first.")
                .font(.system(size: 14))
                .foregroundColor(CombatColors.paper.opacity(0.65))
                .padding(.bottom, 10)

            RingRopeDivider()
                .padding(.bottom, 18)

            ScrollView {
                VStack(spacing: 14) {
                    ForEach(presets, id: \.type) { preset in
                        SportCard(preset: preset) { onPresetSelected(preset) }
                    }
                }
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(CombatColors.night.ignoresSafeArea())
    }
}

private func accentColor(for type: SportType) -> Color {
    switch type {
    case .mmaAmateur, .mmaProRegular, .mmaProTitle:
        return CombatColors.gloveRed
    case .boxingAmateur, .boxingProfessional:
        return CombatColors.impactYellow
    case .judo:
        return CombatColors.burstBlueLight
    case .interval:
        return CombatColors.paper
    }
}

private struct SportCard: View {
    let preset: SportPreset
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 0) {
                accentColor(for: preset.type)
                    .frame(width: 10)

                VStack(alignment: .leading, spacing: 6) {
                    ComicText(text: preset.displayName.uppercased(), fontSize: 18, color: accentColor(for: preset.type), outlineWidth: 1.1)
                    Text(preset.summary)
                        .font(.system(size: 13))
                        .foregroundColor(CombatColors.paper.opacity(0.65))
                }
                .padding(16)

                Spacer()

                Text("\u{203A}")
                    .font(.system(size: 28, weight: .black))
                    .foregroundColor(accentColor(for: preset.type))
                    .padding(.trailing, 16)
            }
            .background(CombatColors.panelNavy)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .overlay(RoundedRectangle(cornerRadius: 10).stroke(CombatColors.ink, lineWidth: 3))
        }
        .buttonStyle(.plain)
    }
}

private extension SportPreset {
    var summary: String {
        let roundsPart: String
        if roundsConfigurable {
            roundsPart = "\(roundsRange.lowerBound)-\(roundsRange.upperBound) rounds"
        } else {
            roundsPart = "\(defaultRounds) round\(defaultRounds > 1 ? "s" : "")"
        }

        let restPart: String
        if hasGoldenScore {
            restPart = ", no rest, golden score"
        } else if restSeconds > 0 {
            restPart = ", \(formatClock(restSeconds)) rest"
        } else {
            restPart = ", no rest"
        }

        let configurablePart: String
        if fullyConfigurable {
            configurablePart = " \u{00B7} configurable"
        } else if roundsConfigurable {
            configurablePart = " \u{00B7} rounds configurable"
        } else {
            configurablePart = ""
        }

        return "\(roundsPart) \u{00D7} \(formatClock(roundSeconds))\(restPart)\(configurablePart)"
    }
}
