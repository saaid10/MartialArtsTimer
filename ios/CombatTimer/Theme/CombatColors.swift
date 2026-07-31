import SwiftUI

/// Shonen-manga fight-poster palette: deep electric blue, punchy glove red,
/// bright impact yellow, and thick black ink outlines — matches the
/// Android app's theme exactly.
enum CombatColors {
    static let ink = Color(hex: 0x0A0A0A)
    static let night = Color(hex: 0x0B1020)
    static let panelNavy = Color(hex: 0x121A33)
    static let panelNavyAlt = Color(hex: 0x1A2547)

    static let burstBlue = Color(hex: 0x1746C4)
    static let burstBlueLight = Color(hex: 0x4B7BFF)
    static let burstBlueDark = Color(hex: 0x0C2A80)

    static let gloveRed = Color(hex: 0xE31E24)
    static let gloveRedDark = Color(hex: 0x8E1216)

    static let impactYellow = Color(hex: 0xFFD400)
    static let impactYellowDark = Color(hex: 0xB88A00)

    static let paper = Color(hex: 0xF5F1E6)
}

extension Color {
    init(hex: UInt32) {
        let red = Double((hex >> 16) & 0xFF) / 255
        let green = Double((hex >> 8) & 0xFF) / 255
        let blue = Double(hex & 0xFF) / 255
        self.init(red: red, green: green, blue: blue)
    }
}
