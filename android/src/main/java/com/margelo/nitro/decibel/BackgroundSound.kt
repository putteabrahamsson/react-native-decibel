package com.margelo.nitro.decibel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log

class BackgroundSound() {

    private var mediaPlayer: MediaPlayer? = null


    fun playBackgroundSound(filePath: String) {
         stopBackgroundSound() 

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(filePath)

                isLooping = true
                prepare()
                start()
            }

            Log.d("BackgroundSound", "Playing background sound: $filePath")

        } catch (e: Exception) {
            Log.e("BackgroundSound", "Failed to play sound: $e")
        }
    }
    
    fun stopBackgroundSound() {
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
            Log.d("BackgroundSound", "Stopped background sound")
        }
    }
}
