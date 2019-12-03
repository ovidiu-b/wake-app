package com.weikappinc.weikapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.weikappinc.weikapp.BaseActivity
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.components.AudioPlayerManager
import com.weikappinc.weikapp.databinding.ActivitySingleAlarmBinding
import com.weikappinc.weikapp.eventbus.events.single_alarm_activity.*
import com.weikappinc.weikapp.presenters.SingleAlarmPresenter
import com.weikappinc.weikapp.setVisibleOrGone
import com.weikappinc.weikapp.showThisShort
import com.weikappinc.weikapp.utils.getFileName
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_single_alarm.*
import kotlinx.android.synthetic.main.audio_alarm_player.*
import kotlinx.android.synthetic.main.day_picker.*
import kotlinx.android.synthetic.main.time_picker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.get
import java.util.concurrent.TimeUnit

private const val IntentBundle = "IntentBundle"
private const val IdAlarmBundle = "IdAlarmBundle"
private const val REQUEST_CODE_CHOOSE_AUDIO = 1

const val MESSAGE_ALARM_ACTIVATED = "MessageAlarm"

class SingleAlarmActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySingleAlarmBinding

    private val presenter: SingleAlarmPresenter = get()

    private var isAlarmNew = false
    private var id: String? = null

    private val disposables = CompositeDisposable()

    private var mPlayer: AudioPlayerManager = AudioPlayerManager()

    private var audioPath: String? = null

    private val rxPermissions: RxPermissions by lazy { RxPermissions(this@SingleAlarmActivity) }

    private val vibrator: Vibrator by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    override fun bindLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_alarm)
    }

    override fun init(savedInstanceState: Bundle?) {
        id = intent?.getBundleExtra(IntentBundle)?.getString(IdAlarmBundle)

        if(id == null) {
            mBinding.title = getString(R.string.add_new_alarm_toolbar_title)

            isAlarmNew = true
        } else {
            mBinding.title = getString(R.string.update_alarm_toolbar_title)

            isAlarmNew = false
        }

        initAlarmDescription()
        initAlarmTime()
        initAlarmDays()
        initAudioPlayer()
    }

    private fun initAlarmTime() {
        hourPicker?.run {
            minValue = 0
            maxValue = 23
            setOnValueChangedListener { _, _, newVal -> presenter.setAlarmHour(newVal) }
        }

        hourPicker.setFormatter {
            String.format("%02d", it)
        }

        minutePicker?.run {
            minValue = 0
            maxValue = 59
            setOnValueChangedListener { _, oldValue, newVal ->
                if(oldValue == 59 && newVal == 0) {
                    hourPicker.value = hourPicker.value + 1

                    presenter.setAlarmHour(hourPicker.value)
                } else if(oldValue == 0 && newVal == 59) {
                    hourPicker.value = hourPicker.value - 1

                    presenter.setAlarmHour(hourPicker.value)
                }

                presenter.setAlarmMinutes(newVal)
            }
        }

        minutePicker.setFormatter {
            String.format("%02d", it)
        }
    }

    private fun initAlarmDescription() {
        alarmDescription.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.setAlarmDescription(s.toString())
            }
        })
    }

    private fun initAlarmDays() {
        val toggleDayListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            when(buttonView.id) {
                R.id.toggleDay1 -> if(isChecked) presenter.addDay(1) else presenter.removeDay(1)
                R.id.toggleDay2 -> if(isChecked) presenter.addDay(2) else presenter.removeDay(2)
                R.id.toggleDay3 -> if(isChecked) presenter.addDay(3) else presenter.removeDay(3)
                R.id.toggleDay4 -> if(isChecked) presenter.addDay(4) else presenter.removeDay(4)
                R.id.toggleDay5 -> if(isChecked) presenter.addDay(5) else presenter.removeDay(5)
                R.id.toggleDay6 -> if(isChecked) presenter.addDay(6) else presenter.removeDay(6)
                R.id.toggleDay7 -> if(isChecked) presenter.addDay(7) else presenter.removeDay(7)
            }

            //changeToggleDayColor(buttonView, isChecked)
        }

        toggleDay1.setOnCheckedChangeListener(toggleDayListener)
        toggleDay2.setOnCheckedChangeListener(toggleDayListener)
        toggleDay3.setOnCheckedChangeListener(toggleDayListener)
        toggleDay4.setOnCheckedChangeListener(toggleDayListener)
        toggleDay5.setOnCheckedChangeListener(toggleDayListener)
        toggleDay6.setOnCheckedChangeListener(toggleDayListener)
        toggleDay7.setOnCheckedChangeListener(toggleDayListener)
    }

    private fun changeToggleDayColor(buttonView: View, isChecked: Boolean) {
        if(isChecked) {
            buttonView.setBackgroundColor(ContextCompat.getColor(this@SingleAlarmActivity,
                R.color.colorAccent
            ))
        } else {
            buttonView.setBackgroundColor(ContextCompat.getColor(this@SingleAlarmActivity,
                R.color.colorSecondary
            ))
        }
    }

    private fun consumerOnNextPermissionReadStorageDenied(granted: Boolean) {
        if(granted) startActivityForResult(AudioChooserActivity.getStartIntent(this@SingleAlarmActivity),
            REQUEST_CODE_CHOOSE_AUDIO
        )
        else onAlarmError(AlarmErrorEvent("Necesita darnos permisos para encontrar los audios de su dispositivo"))
    }

    private fun initAudioPlayer() {
        songVolume.max = 50
        songVolume.progress = 50

        songVolume.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mPlayer.setVolume(getCurrentVolume(songVolume.max - songVolume.progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val chooseAudioEvent = chooseAudio.clicks()
                                .throttleFirst(800, TimeUnit.MILLISECONDS)

        val subscription = chooseAudioEvent
            .compose(rxPermissions.ensure(android.Manifest.permission.READ_EXTERNAL_STORAGE))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                consumerOnNextPermissionReadStorageDenied(it)
            }

        disposables.add(subscription)

        playAudio.setOnClickListener{
            when(mPlayer.isPlaying()) {
                false -> {
                    if (audioPath.isNullOrEmpty()) {
                        onAlarmError(AlarmErrorEvent("Debe seleccionar un audio para reproducirlo"))
                    } else {
                        try{
                            mPlayer.start(audioPath!!)

                            mPlayer.setVolume(getCurrentVolume(songVolume.max - songVolume.progress))

                            mPlayer.player?.setOnCompletionListener { setFabStyleOnPause() }

                            setFabStyleOnStartPlaying()

                        }catch (e: Exception) {
                            onAlarmError(AlarmErrorEvent("No se ha podido cargar el audio"))

                            setFabStyleOnPause()

                            e.printStackTrace()

                            Log.e("SingleAlarmActivity", e.message ?: "Error indefinido intentado cargar un audio en SingleAlarmActivity")
                        }
                    }
                }

                true -> {
                    mPlayer.stop()

                    setFabStyleOnPause()
                }
            }
        }

        disableVibrating.setOnClickListener {
            if(presenter.isAlarmInitialized()) {
                presenter.alarm.isVibrant = false

                setVibrantFabState(false)

                showThisShort("Vibración desactivada")
            }
        }

        enableVibrating.setOnClickListener {
            if(presenter.isAlarmInitialized()) {
                presenter.alarm.isVibrant = true

                setVibrantFabState(true)

                showThisShort("Vibración activada")

                makeDeviceVibrate()
            }
        }
    }

    private fun getCurrentVolume(current: Int) : Float {
        val currentVolume = if(current == songVolume.max) current - 1 else current

        val volume = (Math.log((songVolume.max - currentVolume).toDouble()) / Math.log(songVolume.max.toDouble())).toFloat()

        Log.d("SingleAlarmActivity", "Volume: $volume")

        return volume
    }

    private fun setVibrantFabState(isVibrant: Boolean) {
        if(isVibrant) {
            disableVibrating.setVisibleOrGone(true)
            enableVibrating.setVisibleOrGone(false)
        } else {
            disableVibrating.setVisibleOrGone(false)
            enableVibrating.setVisibleOrGone(true)
        }
    }

    private fun makeDeviceVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500)
        }
    }

    private fun setFabStyleOnStartPlaying() {
        playAudio.setImageDrawable(ContextCompat.getDrawable(this@SingleAlarmActivity,
            R.drawable.ic_pause_accent_24dp
        ))
        playAudio.backgroundTintList = ContextCompat.getColorStateList(this@SingleAlarmActivity, android.R.color.white)
    }

    private fun setFabStyleOnPause() {
        playAudio.setImageDrawable(ContextCompat.getDrawable(this@SingleAlarmActivity,
            R.drawable.ic_play_arrow_white_32dp
        ))
        playAudio.backgroundTintList = ContextCompat.getColorStateList(this@SingleAlarmActivity,
            R.color.colorAccent
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_CHOOSE_AUDIO) {

            if (resultCode == Activity.RESULT_OK) {
                //the selected audio
                audioPath = data?.getStringExtra(RESULT_AUDIO_PATH)

                presenter.setAudioPath(audioPath ?: "")

                audioName.text = getFileName(audioPath ?: "Audio inválido")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmTimeEvent(event: AlarmTimeEvent) {
        hourPicker.value = event.hour
        minutePicker.value = event.minutes
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmTimeRemaining(event: AlarmTimeRemainingEvent) {
        alarm_time_remaining.text = event.timeRemaining
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmDescription(event: AlarmDescriptionEvent) {
        alarmDescription.setText(event.description)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmAudio(event: AlarmAudioEvent) {
        audioPath = event.audioPath
        audioName.text = getFileName(audioPath!!) ?: "No se ha seleccionado ningún audio"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmVibrant(event: AlarmVibrantEvent) {
        setVibrantFabState(event.isVibrant)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmSave(event: AlarmSaveEvent) {
        if(event.finishActivity) {
            val resultIntent = Intent()

            resultIntent.putExtra(MESSAGE_ALARM_ACTIVATED, "¡Alarma establecida!")

            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmError(event: AlarmErrorEvent) {
        val snack = Snackbar.make(mBinding.root, event.message, Snackbar.LENGTH_LONG)

        snack.view.setBackgroundColor(ContextCompat.getColor(this@SingleAlarmActivity, android.R.color.black))

        val tvMessage = snack.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            tvMessage.textAlignment = View.TEXT_ALIGNMENT_CENTER
        } else {
            tvMessage.gravity = Gravity.CENTER_HORIZONTAL
        }

        snack.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAlarmDayEvent(event: AlarmDayEvent) {
        if(event.days.indexOf(1) != -1) {
            toggleDay1.isChecked = true
            changeToggleDayColor(toggleDay1, true)
        } else {
            toggleDay1.isChecked = false
            changeToggleDayColor(toggleDay1, false)
        }

        if(event.days.indexOf(2) != -1) {
            toggleDay2.isChecked = true
            changeToggleDayColor(toggleDay2, true)
        } else {
            toggleDay2.isChecked = false
            changeToggleDayColor(toggleDay2, false)
        }

        if(event.days.indexOf(3) != -1) {
            toggleDay3.isChecked = true
            changeToggleDayColor(toggleDay3, true)
        } else {
            toggleDay3.isChecked = false
            changeToggleDayColor(toggleDay3, false)
        }

        if(event.days.indexOf(4) != -1) {
            toggleDay4.isChecked = true
            changeToggleDayColor(toggleDay4, true)
        } else {
            toggleDay4.isChecked = false
            changeToggleDayColor(toggleDay4, false)
        }

        if(event.days.indexOf(5) != -1) {
            toggleDay5.isChecked = true
            changeToggleDayColor(toggleDay5, true)
        } else {
            toggleDay5.isChecked = false
            changeToggleDayColor(toggleDay5, false)
        }

        if(event.days.indexOf(6) != -1) {
            toggleDay6.isChecked = true
            changeToggleDayColor(toggleDay6, true)
        } else {
            toggleDay6.isChecked = false
            changeToggleDayColor(toggleDay6, false)
        }

        if(event.days.indexOf(7) != -1) {
            toggleDay7.isChecked = true
            changeToggleDayColor(toggleDay7, true)
        } else {
            toggleDay7.isChecked = false
            changeToggleDayColor(toggleDay7, false)
        }
    }

    override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)

        presenter.loadAlarm(id)
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)

        mPlayer.release()

        setFabStyleOnPause()

        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDispose()
    }

    override fun getHomeIcon(): Int = R.drawable.ic_close_white_24dp

    override fun getMenuLayout(): Int = R.menu.toolbar_ok_menu

    override fun onMenuAction(id_item: Int) {
        when(id_item) {
            R.id.action_ok -> {
                presenter.saveAlarm()
            }
        }
    }

    companion object {
        fun getStartIntent(context: Context, idAlarm: String?): Intent {
            val intent = Intent(context, SingleAlarmActivity::class.java)

            intent.putExtra(IntentBundle, Bundle().apply {
                putString(IdAlarmBundle, idAlarm)
            })

            return intent
        }
    }
}
