import SwiftUI

private enum Screen {
    case selection
    case config(SportPreset)
    case timer
}

struct ContentView: View {
    @StateObject private var viewModel = TimerViewModel()
    @State private var screen: Screen = .selection

    var body: some View {
        switch screen {
        case .selection:
            SportSelectionScreen(presets: SportPresets.all) { preset in
                if preset.isConfigurable {
                    screen = .config(preset)
                } else {
                    viewModel.configure(preset: preset)
                    screen = .timer
                }
            }

        case .config(let preset):
            ConfigScreen(preset: preset, onBack: { screen = .selection }) { rounds, roundSeconds, restSeconds in
                viewModel.configure(preset: preset, rounds: rounds, roundSeconds: roundSeconds, restSeconds: restSeconds)
                screen = .timer
            }

        case .timer:
            TimerScreen(viewModel: viewModel) { screen = .selection }
        }
    }
}
