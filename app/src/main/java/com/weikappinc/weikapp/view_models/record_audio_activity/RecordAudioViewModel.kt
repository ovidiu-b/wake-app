package com.weikappinc.weikapp.view_models.record_audio_activity

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.components.Chronometer
import com.weikappinc.weikapp.components.ChronometherListener

private const val TAG = "RecordAudioViewModel"

class RecordAudioViewModel(val app: Application) : AndroidViewModel(app){

    private val title = app.getString(R.string.record_audio_activity_toolbar_title).toUpperCase()

    val toolbarTitle = MutableLiveData<String>(title)

    private val chrono = Chronometer((object: ChronometherListener {
        override fun onTimeChange(time: String) {
            toolbarTitle.postValue(time)
        }
    }), title)

    // El usuario ha pulsado el botón de grabación o la actividad ha vuelto a la vista principal
    fun startChronometer() {
        chrono.start()
    }

    // La actividad se ha ido de la vista principal
    fun pauseChronometer() {
        chrono.pause()
    }

    // El usuarió ha puslado el botón para parar la grabación
    fun stopChronometer() {
        chrono.stop()
    }

    companion object {
        fun create(activity: AppCompatActivity) = ViewModelProviders.of(activity).get(RecordAudioViewModel::class.java)
    }
}
