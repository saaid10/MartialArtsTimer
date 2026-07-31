import Foundation

enum SportType: Hashable {
    case mmaAmateur
    case mmaProRegular
    case mmaProTitle
    case boxingAmateur
    case boxingProfessional
    case judo
    case interval
}

/// A fixed sport ruleset. Only `.interval` allows configuring round/rest
/// duration; only `.boxingProfessional` allows configuring the round count.
/// Every other sport starts exactly as defined here.
struct SportPreset: Equatable {
    let type: SportType
    let displayName: String
    let defaultRounds: Int
    let roundSeconds: Int
    let restSeconds: Int
    var hasGoldenScore: Bool = false
    var roundsConfigurable: Bool = false
    var roundsRange: ClosedRange<Int> = 1...1
    var fullyConfigurable: Bool = false

    var isConfigurable: Bool { roundsConfigurable || fullyConfigurable }
}

enum SportPresets {
    static let mmaAmateur = SportPreset(
        type: .mmaAmateur,
        displayName: "MMA Amateur",
        defaultRounds: 3,
        roundSeconds: 180,
        restSeconds: 60
    )

    static let mmaProRegular = SportPreset(
        type: .mmaProRegular,
        displayName: "MMA Pro (Regular Fight)",
        defaultRounds: 3,
        roundSeconds: 300,
        restSeconds: 60
    )

    static let mmaProTitle = SportPreset(
        type: .mmaProTitle,
        displayName: "MMA Pro (Title Fight)",
        defaultRounds: 5,
        roundSeconds: 300,
        restSeconds: 60
    )

    static let boxingAmateur = SportPreset(
        type: .boxingAmateur,
        displayName: "Boxing Amateur",
        defaultRounds: 3,
        roundSeconds: 180,
        restSeconds: 60
    )

    static let boxingProfessional = SportPreset(
        type: .boxingProfessional,
        displayName: "Boxing Professional",
        defaultRounds: 4,
        roundSeconds: 180,
        restSeconds: 60,
        roundsConfigurable: true,
        roundsRange: 4...12
    )

    static let judo = SportPreset(
        type: .judo,
        displayName: "Judo",
        defaultRounds: 1,
        roundSeconds: 240,
        restSeconds: 0,
        hasGoldenScore: true
    )

    static let interval = SportPreset(
        type: .interval,
        displayName: "Interval Setting",
        defaultRounds: 3,
        roundSeconds: 180,
        restSeconds: 60,
        fullyConfigurable: true
    )

    static let all: [SportPreset] = [
        mmaAmateur,
        mmaProRegular,
        mmaProTitle,
        boxingAmateur,
        boxingProfessional,
        judo,
        interval
    ]
}
