package com.margelo.nitro.decibel

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.decibel.BackgroundSound

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.util.*
import kotlin.math.log10
import kotlin.math.sqrt


@DoNotStrip
class Decibel : HybridDecibelSpec() {
  private var backgroundSound: BackgroundSound? = BackgroundSound()
  private var audioRecord: AudioRecord? = null
  private var bufferSize = 0
  private var isRecording = false
  private var timer: Timer? = null
  private val listeners = mutableListOf<(Double) -> Unit>()

  override fun requestPermission(): Promise<String> {
    return Promise.resolved("requested_on_JS_thread")
  }

  override fun start(interval: Double?) {
    val period = interval?.times(1000)?.toLong() ?: 200L
    bufferSize = AudioRecord.getMinBufferSize(
          44100,
          AudioFormat.CHANNEL_IN_MONO,
          AudioFormat.ENCODING_PCM_16BIT
    )

    audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    audioRecord?.startRecording()
    isRecording = true

    timer = Timer()
    timer?.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            val buffer = ShortArray(bufferSize)
            val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
            if (read > 0) {
                var sum = 0.0
                for (i in 0 until read) {
                    sum += buffer[i] * buffer[i]
                }
                val rms = sqrt(sum / read)

                // set dB to negative value, similar to iOS (-160 → 0)
                val maxAmplitude = 32768.0 
                val dB = if (rms > 0) 20 * log10(rms / maxAmplitude) else -160.0

                listeners.forEach { it(dB) }
            }
        }
    }, 0L, period)
  }

  override fun stop() {
    timer?.cancel()
    timer = null
    isRecording = false
    audioRecord?.stop()
    audioRecord?.release()
    audioRecord = null
  }

  override fun playBackgroundSound(filePath: String) {
    backgroundSound?.playBackgroundSound(filePath)
  }

  override fun stopBackgroundSound() {
    backgroundSound?.stopBackgroundSound()
  }

  override fun onDecibelUpdate(listener: (Double) -> Unit) {
    listeners?.add(listener)
  }

  override fun removeDecibelUpdateListener(listener: (Double) -> Unit) {
    listeners?.clear()
  }
}
