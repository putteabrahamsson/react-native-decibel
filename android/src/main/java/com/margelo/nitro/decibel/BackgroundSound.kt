import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

class BackgroundSound(private val context: Context) {

    private var player: ExoPlayer? = null

    /**
     * Spela ljudfil från URI (lokal fil eller nätverk)
     */
    fun playBackgroundSound(filePath: String) {
        stopBackgroundSound() // stoppa eventuell befintlig uppspelning

        try {
            val uri = Uri.parse(filePath)
            player = ExoPlayer.Builder(context).build().also { exo ->
                val mediaItem = MediaItem.fromUri(uri)
                exo.setMediaItem(mediaItem)
                exo.repeatMode = Player.REPEAT_MODE_OFF
                exo.playWhenReady = true
                exo.prepare()
            }

            Log.d("BackgroundSound", "Playing background sound: $filePath")
        } catch (e: Exception) {
            Log.e("BackgroundSound", "Failed to play background sound: $e")
        }
    }

    /**
     * Pausa uppspelningen
     */
    fun pauseBackgroundSound() {
        player?.pause()
        Log.d("BackgroundSound", "Paused background sound")
    }

    /**
     * Återuppta uppspelningen
     */
    fun resumeBackgroundSound() {
        player?.play()
        Log.d("BackgroundSound", "Resumed background sound")
    }

    /**
     * Stoppa och släpp resurser
     */
    fun stopBackgroundSound() {
        player?.release()
        player = null
        Log.d("BackgroundSound", "Stopped background sound")
    }
}
