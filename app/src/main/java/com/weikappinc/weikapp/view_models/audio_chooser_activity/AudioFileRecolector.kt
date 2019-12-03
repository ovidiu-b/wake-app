package com.weikappinc.weikapp.view_models.audio_chooser_activity

import android.content.Context
import android.provider.MediaStore
import com.weikappinc.weikapp.adapters.audio_chooser_activity.AudioItemPreview
import com.weikappinc.weikapp.utils.getFileName

class AudioFileRecolector(private val activity: Context) {

    fun getAudioItems() : MutableList<AudioItemPreview> {
        val audioItems: MutableList<AudioItemPreview> = mutableListOf()

        val proj = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA)

        val audioCursor =
            activity.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null)

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    val audioId = audioCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                    val audioPath = audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

                    audioItems.add(
                        AudioItemPreview(
                            audioCursor.getLong(audioId).toString(),
                            getFileName(audioCursor.getString(audioPath)) ?: "Sin título",
                            audioCursor.getString(audioPath) ?: "Sin ruta"
                        )
                    )

                } while (audioCursor.moveToNext())
            }
        }

        audioCursor?.close()

        return audioItems.toHashSet().sortedWith(compareBy(AudioItemPreview::name)).toMutableList()
    }
}
