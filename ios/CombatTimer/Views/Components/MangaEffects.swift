import SwiftUI

/// A tiny deterministic PRNG so the speed-line burst renders identically on
/// every draw (Swift's SystemRandomNumberGenerator isn't seedable).
struct SeededGenerator: RandomNumberGenerator {
    private var state: UInt64
    init(seed: UInt64) { state = seed &+ 0x9E3779B97F4A7C15 }
    mutating func next() -> UInt64 {
        state ^= state >> 12
        state ^= state << 25
        state ^= state >> 27
        return state &* 2685821657736338717
    }
}

/// Radiating lines bursting from a center point, like a manga cover's
/// impact flash behind the main figure. Meant to sit behind a focal panel.
struct SpeedLineBurst: View {
    var lineColor: Color = .white
    var lineCount: Int = 56
    var baseAlpha: Double = 0.35

    var body: some View {
        Canvas { context, size in
            let center = CGPoint(x: size.width / 2, y: size.height / 2)
            let maxRadius = (size.width * size.width + size.height * size.height).squareRoot() / 2
            var rng = SeededGenerator(seed: 42)

            for i in 0..<lineCount {
                let angle = 2 * Double.pi * Double(i) / Double(lineCount)
                let innerRadius = maxRadius * 0.10
                let outerRadius = maxRadius * (0.55 + Double.random(in: 0...1, using: &rng) * 0.5)
                let start = CGPoint(x: center.x + cos(angle) * innerRadius, y: center.y + sin(angle) * innerRadius)
                let end = CGPoint(x: center.x + cos(angle) * outerRadius, y: center.y + sin(angle) * outerRadius)
                let strokeWidth = maxRadius * (0.006 + Double.random(in: 0...1, using: &rng) * 0.012)

                var path = Path()
                path.move(to: start)
                path.addLine(to: end)
                context.stroke(
                    path,
                    with: .color(lineColor.opacity(baseAlpha * (0.5 + Double.random(in: 0...1, using: &rng) * 0.5))),
                    style: StrokeStyle(lineWidth: strokeWidth, lineCap: .round)
                )
            }
        }
    }
}

/// A boxing-ring rope divider, drawn with a black ink outline like a comic panel border.
struct RingRopeDivider: View {
    var height: CGFloat = 12

    var body: some View {
        Canvas { context, size in
            let stripeHeight = size.height / 5
            let colors = [CombatColors.gloveRed, CombatColors.impactYellow, CombatColors.gloveRed]
            for (index, color) in colors.enumerated() {
                let y = stripeHeight * CGFloat(index * 2 + 1)
                var path = Path()
                path.move(to: CGPoint(x: 0, y: y))
                path.addLine(to: CGPoint(x: size.width, y: y))
                context.stroke(path, with: .color(CombatColors.ink), style: StrokeStyle(lineWidth: stripeHeight * 1.1))
                context.stroke(path, with: .color(color), style: StrokeStyle(lineWidth: stripeHeight * 0.75))
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
    }
}

/// Bold comic-book lettering: a black-ink outline behind a solid fill
/// color, like the title lettering on a shonen manga cover. SwiftUI has no
/// native text stroke, so the outline is faked with 8 offset copies.
struct ComicText: View {
    let text: String
    let fontSize: CGFloat
    let color: Color
    var outlineColor: Color = CombatColors.ink
    var outlineWidth: CGFloat = 1.4
    var tracking: CGFloat = 0

    private static let directions: [CGSize] = [
        CGSize(width: -1, height: -1), CGSize(width: 0, height: -1), CGSize(width: 1, height: -1),
        CGSize(width: -1, height: 0), CGSize(width: 1, height: 0),
        CGSize(width: -1, height: 1), CGSize(width: 0, height: 1), CGSize(width: 1, height: 1)
    ]

    var body: some View {
        ZStack {
            ForEach(Array(Self.directions.enumerated()), id: \.offset) { _, direction in
                Text(text)
                    .font(.system(size: fontSize, weight: .black))
                    .tracking(tracking)
                    .foregroundColor(outlineColor)
                    .offset(x: direction.width * outlineWidth, y: direction.height * outlineWidth)
            }
            Text(text)
                .font(.system(size: fontSize, weight: .black))
                .tracking(tracking)
                .foregroundColor(color)
        }
        .fixedSize()
    }
}
