import SwiftUI

private let sevenSegmentMap: [Character: Set<Character>] = [
    "0": ["a", "b", "c", "d", "e", "f"],
    "1": ["b", "c"],
    "2": ["a", "b", "g", "e", "d"],
    "3": ["a", "b", "g", "c", "d"],
    "4": ["f", "g", "b", "c"],
    "5": ["a", "f", "g", "c", "d"],
    "6": ["a", "f", "g", "e", "c", "d"],
    "7": ["a", "b", "c"],
    "8": ["a", "b", "c", "d", "e", "f", "g"],
    "9": ["a", "b", "c", "d", "f", "g"]
]

/// A digital-clock style readout built from drawn segments (7 per digit,
/// plus a colon), instead of a text glyph. Unlit segments are drawn faintly
/// so the digits read like an old ring/scoreboard clock rather than plain text.
struct SevenSegmentDisplay: View {
    let value: String
    var digitHeight: CGFloat = 88
    var onColor: Color = Color(hex: 0xFF3B30)
    var offColor: Color? = nil

    private var resolvedOffColor: Color { offColor ?? onColor.opacity(0.12) }

    var body: some View {
        HStack(spacing: digitHeight * 0.12) {
            ForEach(Array(value.enumerated()), id: \.offset) { _, char in
                if char.isNumber {
                    SevenSegmentDigit(digit: char, height: digitHeight, onColor: onColor, offColor: resolvedOffColor)
                } else if char == ":" {
                    ColonSeparator(height: digitHeight, color: onColor)
                } else {
                    Spacer().frame(width: digitHeight * 0.22)
                }
            }
        }
    }
}

private struct SevenSegmentDigit: View {
    let digit: Character
    let height: CGFloat
    let onColor: Color
    let offColor: Color

    var body: some View {
        Canvas { context, size in
            let active = sevenSegmentMap[digit] ?? []
            let w = size.width
            let h = size.height
            let thickness = w * 0.22
            let inset = thickness * 0.55
            let x0 = inset
            let x2 = w - inset
            let y0 = inset
            let y2 = h - inset
            let ym = h / 2

            func segment(_ key: Character, _ start: CGPoint, _ end: CGPoint) {
                var path = Path()
                path.move(to: start)
                path.addLine(to: end)
                context.stroke(
                    path,
                    with: .color(active.contains(key) ? onColor : offColor),
                    style: StrokeStyle(lineWidth: thickness, lineCap: .round)
                )
            }

            segment("a", CGPoint(x: x0, y: y0), CGPoint(x: x2, y: y0))
            segment("b", CGPoint(x: x2, y: y0), CGPoint(x: x2, y: ym))
            segment("c", CGPoint(x: x2, y: ym), CGPoint(x: x2, y: y2))
            segment("d", CGPoint(x: x0, y: y2), CGPoint(x: x2, y: y2))
            segment("e", CGPoint(x: x0, y: ym), CGPoint(x: x0, y: y2))
            segment("f", CGPoint(x: x0, y: y0), CGPoint(x: x0, y: ym))
            segment("g", CGPoint(x: x0, y: ym), CGPoint(x: x2, y: ym))
        }
        .frame(width: height * 0.56, height: height)
    }
}

private struct ColonSeparator: View {
    let height: CGFloat
    let color: Color

    var body: some View {
        let dot = height * 0.11
        VStack(spacing: height * 0.18) {
            Circle().fill(color).frame(width: dot, height: dot)
            Circle().fill(color).frame(width: dot, height: dot)
        }
        .frame(height: height)
    }
}
