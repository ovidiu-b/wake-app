package com.weikappinc.weikapp.adapters.audio_chooser_activity

data class AudioItemPreview (val id: String, val name: String, val path: String) {
    var isChecked: Boolean = false
    var isPlaying: Boolean = false
}