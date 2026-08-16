package com.saee.combattimer.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.SoundPool
import com.saee.combattimer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Plays all timer sound cues by loading recorded res/raw clips into a
 * SoundPool for low-latency, precisely-timed playback.
 */
class SoundEngine(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Cues use the MEDIA stream/usage rather than ALARM: on this device (and apparently more
    // broadly - confirmed via `dumpsys audio`) the ALARM stream has no dedicated per-device volume
    // bucket for a Bluetooth A2DP speaker, so it silently falls back to a low, stuck "default"
    // bucket that setStreamVolume can't reach - cues stayed quiet on a connected gym speaker no
    // matter how loud we set STREAM_ALARM. MEDIA doesn't have that problem: Bluetooth speakers
    // implement real volume sync (AVRCP) for it, since every music app depends on it. It's still
    // unaffected by the ringer/silent switch (only Ring/Notification/System streams are), so the
    // "audible with the phone silenced" requirement still holds.
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    // Sharing STREAM_MUSIC with whatever else the user is playing (e.g. Spotify during sparring)
    // means there's only one shared volume level to turn up/down - forcing it to max would blast
    // their music too. Instead, request transient "duck" focus for just the moment each cue plays:
    // the system automatically lowers other apps' playback so the cue cuts through clearly, then
    // hands full volume straight back once that clip ends. No volume level gets touched directly,
    // and music stays at full volume the rest of the round (not just outside the app).
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(audioAttributes)
        .setWillPauseWhenDucked(false)
        .build()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var duckingJob: Job? = null

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(audioAttributes)
        .build()

    private val loadedSoundIds = mutableSetOf<Int>()

    // Registered before any load() call below: SoundPool loads happen on a background thread and
    // can complete (and fire this listener) before the next line of the constructor even runs.
    // Attaching the listener after load() risks losing that completion entirely - loadedSoundIds
    // would then never contain the id, so playSample() silently no-ops on every call, forever.
    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedSoundIds += sampleId
        }
    }

    private val clapperSoundId = soundPool.load(context, R.raw.wood_clap_triple, 1)
    private val roundEndHornSoundId = soundPool.load(context, R.raw.mma_horn, 1)
    private val countdownBeepSoundId = soundPool.load(context, R.raw.timer_beep, 1)
    private val finalCountdownBeepSoundId = soundPool.load(context, R.raw.boxing_bell, 1)

    // Measured from the actual clip so the post-duck tail below starts right as the sound ends
    // rather than guessing a fixed cue length. mma_horn.mp3 is the exception: ffmpeg silencedetect
    // shows it's clearly loud only up to ~1.4-1.8s, then decays quietly until the file actually
    // ends at ~2.7s - using the full file length left the duck held through a second-plus of
    // near-silent tail that read as "stuck low" rather than "still playing." Cut to the perceptual
    // end instead; this only affects when the duck releases, not what actually gets played.
    private val cueDurationsMs = mapOf(
        clapperSoundId to durationOf(context, R.raw.wood_clap_triple),
        roundEndHornSoundId to HORN_PERCEIVED_DURATION_MS,
        countdownBeepSoundId to durationOf(context, R.raw.timer_beep),
        finalCountdownBeepSoundId to durationOf(context, R.raw.boxing_bell),
    )

    private fun durationOf(context: Context, resId: Int): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            context.resources.openRawResourceFd(resId).use { afd ->
                retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: DEFAULT_CUE_DURATION_MS
        } catch (e: Exception) {
            DEFAULT_CUE_DURATION_MS
        } finally {
            retriever.release()
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

    /**
     * Call ~2 seconds before a cue is due, so the duck has settled in before the sound hits
     * instead of snapping in right as it starts. Caller supplies the lead time it actually has;
     * this waits out the difference down to [PRE_DUCK_LEAD_MS] and engages then. Includes its own
     * safety-net release in case the expected cue never actually plays (e.g. the round gets
     * paused or reset between this call and the real one) so the duck can't get stuck forever.
     */
    fun prepareDuck(leadMs: Long) {
        duckingJob?.cancel()
        duckingJob = scope.launch {
            delay((leadMs - PRE_DUCK_LEAD_MS).coerceAtLeast(0))
            audioManager.requestAudioFocus(focusRequest)
            delay(SAFETY_NET_HOLD_MS)
            audioManager.abandonAudioFocusRequest(focusRequest)
        }
    }

    private fun playSample(soundId: Int) {
        if (soundId in loadedSoundIds) {
            // Replaces prepareDuck()'s pending job (or a previous cue's) rather than stacking:
            // requesting focus again while already ducked is a harmless no-op, and only the
            // freshly-scheduled abandon below - timed off this specific clip - should fire.
            duckingJob?.cancel()
            audioManager.requestAudioFocus(focusRequest)
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            duckingJob = scope.launch {
                delay((cueDurationsMs[soundId] ?: DEFAULT_CUE_DURATION_MS) + POST_DUCK_TAIL_MS)
                audioManager.abandonAudioFocusRequest(focusRequest)
            }
        }
    }

    fun release() {
        scope.cancel()
        soundPool.release()
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    private companion object {
        const val DEFAULT_CUE_DURATION_MS = 1500L
        // See mma_horn.mp3's entry in cueDurationsMs above.
        const val HORN_PERCEIVED_DURATION_MS = 1800L
        // How long before a cue starts / after it ends the duck should hold, so the dip doesn't
        // snap in and out right on top of the sound - an exact-aligned duck sounded jarring.
        const val PRE_DUCK_LEAD_MS = 1500L
        const val POST_DUCK_TAIL_MS = 1500L
        const val SAFETY_NET_HOLD_MS = 4000L
    }
}
