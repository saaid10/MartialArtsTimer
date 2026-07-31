import Foundation

enum TimerPhase: Hashable {
    case idle
    case round
    case rest
    case awaitingGoldenScoreDecision
    case goldenScore
    case finished
}
