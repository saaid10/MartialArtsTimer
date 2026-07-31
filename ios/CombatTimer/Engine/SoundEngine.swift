import AVFoundation

/// Plays all timer sound cues from bundled clips, preloaded at init for
/// low-latency, precisely-timed playback. The audio session category is
/// `.playback` so cues are still heard even if the phone's silent switch
/// is on, matching the Android version's ALARM stream usage.
final class SoundEngine {
    private let clapperPlayer: AVAudioPlayer?
    private let roundEndHornPlayer: AVAudioPlayer?
    private let countdownBeepPlayer: AVAudioPlayer?
    private let finalCountdownPlayer: AVAudioPlayer?

    init() {
        try? AVAudioSession.sharedInstance().setCategory(.playback, options: [.mixWithOthers])
        try? AVAudioSession.sharedInstance().setActive(true)

        clapperPlayer = Self.loadPlayer(named: "wood_clap_triple", ext: "mp3")
        roundEndHornPlayer = Self.loadPlayer(named: "mma_horn", ext: "wav")
        countdownBeepPlayer = Self.loadPlayer(named: "timer_beep", ext: "mp3")
        finalCountdownPlayer = Self.loadPlayer(named: "boxing_bell", ext: "mp3")
    }

    /// The "10-second clapper": the recorded triple wood-clap warning at
    /// 10 seconds remaining in a round.
    func playTenSecondClapper() { play(clapperPlayer) }

    /// The recorded air horn played when a round ends.
    func playRoundEndHorn() { play(roundEndHornPlayer) }

    /// Single beep for the rest-period countdown at 5, 4, 3, 2, 1 seconds remaining.
    func playCountdownBeep() { play(countdownBeepPlayer) }

    /// Boxing bell marking the round starting again after rest.
    func playFinalCountdownBeep() { play(finalCountdownPlayer) }

    private func play(_ player: AVAudioPlayer?) {
        guard let player else { return }
        player.currentTime = 0
        player.play()
    }

    private static func loadPlayer(named name: String, ext: String) -> AVAudioPlayer? {
        guard let url = Bundle.main.url(forResource: name, withExtension: ext) else { return nil }
        let player = try? AVAudioPlayer(contentsOf: url)
        player?.prepareToPlay()
        return player
    }
}
