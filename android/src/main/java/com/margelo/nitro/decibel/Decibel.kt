package com.margelo.nitro.decibel

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.util.*
import kotlin.math.log10
import kotlin.math.sqrt


@DoNotStrip
class Decibel : HybridDecibelSpec() {
  private var audioRecord: AudioRecord? = null
  private var bufferSize = 0
  private var isRecording = false
  private var timer: Timer? = null
  private val listeners = mutableListOf<(Double) -> Unit>()

  override fun requestPermission(): Promise<String> {
    return Promise.resolved("requested_on_JS_thread")
  }

  override fun start(interval: Double?) {
    // Always stop and clean up any existing recording first
    stop()

    val period = interval?.times(1000)?.toLong() ?: 200L
    bufferSize = AudioRecord.getMinBufferSize(
          44100,
          AudioFormat.CHANNEL_IN_MONO,
          AudioFormat.ENCODING_PCM_16BIT
    )

    try {
      // Create a completely new AudioRecord instance
      audioRecord = AudioRecord(
          MediaRecorder.AudioSource.MIC,
          44100,
          AudioFormat.CHANNEL_IN_MONO,
          AudioFormat.ENCODING_PCM_16BIT,
          bufferSize
      )

      // Check if AudioRecord was initialized properly
      if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
        Log.e("Decibel", "AudioRecord initialization failed")
        audioRecord?.release()
        audioRecord = null
        return
      }

      audioRecord?.startRecording()
      isRecording = true

      timer = Timer()
      timer?.scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
              if (!isRecording || audioRecord == null) {
                  return
              }

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
              } else if (read < 0) {
                  Log.e("Decibel", "Error reading audio data: $read")
              }
          }
      }, 0L, period)
    } catch (e: Exception) {
      Log.e("Decibel", "Failed to start recording: ${e.message}")
      stop()
    }
  }

  override fun stop() {
    timer?.cancel()
    timer = null
    isRecording = false
    
    try {
      audioRecord?.stop()
    } catch (e: Exception) {
      Log.e("Decibel", "Error stopping AudioRecord: ${e.message}")
    }
    
    audioRecord?.release()
    audioRecord = null
  }

  override fun onDecibelUpdate(listener: (Double) -> Unit) {
    listeners?.add(listener)
  }

  override fun removeDecibelUpdateListener(listener: (Double) -> Unit) {
    listeners?.clear()
  }
}
