package com.saee.combattimer.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.saee.combattimer.R

/**
 * Plays all timer sound cues by loading recorded res/raw clips into a
 * SoundPool for low-latency, precisely-timed playback.
 */
class SoundEngine(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSoundIds = mutableSetOf<Int>()
    private val clapperSoundId = soundPool.load(context, R.raw.wood_clap_triple, 1)
    private val roundEndHornSoundId = soundPool.load(context, R.raw.mma_horn, 1)
    private val countdownBeepSoundId = soundPool.load(context, R.raw.timer_beep, 1)
    private val finalCountdownBeepSoundId = soundPool.load(context, R.raw.boxing_bell, 1)

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedSoundIds += sampleId
        }
    }

    /**
     * The "10-second clapper": the recorded triple wood-clap warning at
     * 10 seconds remaining in a round.
     */
    fun playTenSecondClapper() = playSample(clapperSoundId)

    /** The recorded air horn played when a round ends. */
    fun playRoundEndHorn() = playSample(roundEndHornSoundId)

    /** Single beep for the rest-period countdown at 5, 4, 3, 2 seconds remaining. */
    fun playCountdownBeep() = playSample(countdownBeepSoundId)

    /** Boxing bell marking the round starting again after rest. */
    fun playFinalCountdownBeep() = playSample(finalCountdownBeepSoundId)

    private fun playSample(soundId: Int) {
        if (soundId in loadedSoundIds) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
