package com.weikappinc.weikapp.components

import android.media.AudioAttributes
import android.media.MediaPlayer

class AudioPlayerManager {
    var player: MediaPlayer? = null

    fun isPlaying() : Boolean {
        return try {
            player?.isPlaying ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun start(audioPath: String) {
        if(player == null) {
            player = MediaPlayer()
        } else {
            player?.release()

            player = null

            player = MediaPlayer()
        }

        player?.run {
            setAudioAttributes(
                AudioAttributes.
                    Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )

            setDataSource(audioPath)
        }

        player?.run {
            prepareAsync()

            setOnPreparedListener { player?.start() }
        }
    }

    fun setVolume(value: Float) {
        try {
            player?.setVolume(value, value)
        } catch (e: Exception) {}
    }

    fun stop() {
        try {
            player?.stop()
        }catch (e: IllegalStateException) {}
    }

    fun release() {
        player?.release()
    }
}
