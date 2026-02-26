package com.bubbzeniac.apssof.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import com.bubbzeniac.apssof.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var popJob: Job? = null
    private var popTrack: AudioTrack? = null

    private var ambientPlayer: MediaPlayer? = null

    private var soundsEnabled: Boolean = true
    private var currentVolume: Float = 0.7f

    init {
        scope.launch(Dispatchers.IO) { initPopSound() }
    }

    private fun initPopSound() {
        try {
            val sampleRate = 44100
            val durationMs = 90
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / sampleRate
                val envelope = exp(-t * 28f)
                val freq = 700f - 500f * (i.toFloat() / numSamples)
                val sample = sin(2.0 * PI * freq * t) * envelope * Short.MAX_VALUE * 0.85
                buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
            }

            val minBufSize = AudioTrack.getMinBufferSize(
                sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(maxOf(numSamples * 2, minBufSize))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            popTrack = track
        } catch (_: Exception) {}
    }

    fun playBubblePop() {
        if (!soundsEnabled) return
        popJob?.cancel()
        popJob = scope.launch(Dispatchers.IO) {
            try {
                val track = popTrack ?: return@launch
                synchronized(track) {
                    if (track.state != AudioTrack.STATE_INITIALIZED) return@launch
                    try {
                        if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                            track.stop()
                        }
                        track.reloadStaticData()
                        track.setVolume(currentVolume)
                        track.play()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {}
        }
    }

    fun startAmbient(type: AmbientType, volume: Float) {
        stopAmbient()
        currentVolume = volume
        if (type == AmbientType.SILENCE || !soundsEnabled) return

        val resId = when (type) {
            AmbientType.OCEAN -> R.raw.ocean
            AmbientType.RAIN -> R.raw.rain
            AmbientType.WIND -> R.raw.wind
            AmbientType.SILENCE -> return
        }

        try {
            ambientPlayer = MediaPlayer.create(context, resId)?.apply {
                isLooping = true
                setVolume(volume, volume)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                start()
            }
        } catch (_: Exception) {}
    }

    fun stopAmbient() {
        try {
            ambientPlayer?.let { player ->
                if (player.isPlaying) player.stop()
                player.release()
            }
        } catch (_: Exception) {}
        ambientPlayer = null
    }

    fun pauseAmbient() {
        try {
            if (ambientPlayer?.isPlaying == true) {
                ambientPlayer?.pause()
            }
        } catch (_: Exception) {}
    }

    fun resumeAmbient() {
        try {
            ambientPlayer?.start()
        } catch (_: Exception) {}
    }

    fun isAmbientActuallyPlaying(): Boolean {
        return try {
            ambientPlayer?.isPlaying == true
        } catch (_: Exception) {
            false
        }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume
        try {
            ambientPlayer?.setVolume(volume, volume)
        } catch (_: Exception) {}
    }

    fun setEnabled(enabled: Boolean) {
        soundsEnabled = enabled
        if (!enabled) {
            stopAmbient()
        }
    }

    fun release() {
        stopAmbient()
        try {
            popTrack?.stop()
            popTrack?.release()
        } catch (_: Exception) {}
        popTrack = null
    }
}
