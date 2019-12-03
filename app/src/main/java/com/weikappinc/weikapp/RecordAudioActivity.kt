package com.weikappinc.weikapp

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.weikappinc.weikapp.databinding.ActivityRecordAudioBinding
import com.weikappinc.weikapp.view_models.record_audio_activity.RecordAudioViewModel
import kotlinx.android.synthetic.main.activity_record_audio.*
import kotlinx.android.synthetic.main.toolbar_close_title_ok.*

private const val STATE_IS_RECORDING_AUDIO = "IsRecordingAudio"

class RecordAudioActivity : BaseActivity() {

    private val viewModel: RecordAudioViewModel by lazy { RecordAudioViewModel.create(this) }

    private val mBinding: ActivityRecordAudioBinding by lazy {
        DataBindingUtil.setContentView<ActivityRecordAudioBinding>(this, R.layout.activity_record_audio)
    }

    private var isRecordingAudio = false

    override fun bindLayout() {
        mBinding.recordAudioListener = (object: RecordAudioListener {
            override fun onRecordButtonClicked(v: View) {
                isRecordingAudio = !isRecordingAudio

                if(isRecordingAudio) {
                    viewModel.startChronometer()
                } else {
                    viewModel.stopChronometer()
                }

                updateUI()
            }
        })

        mBinding.lifecycleOwner = this

        mBinding.viewModel = viewModel
    }

    override fun init(savedInstanceState: Bundle?) {
        isRecordingAudio = savedInstanceState?.getBoolean(STATE_IS_RECORDING_AUDIO) ?: false

        if(savedInstanceState != null) updateUI()
    }

    private fun updateUI() {
        setToolbarTitleState()
        setRecordButtonState()
    }

    private fun setToolbarTitleState() {
        when(isRecordingAudio) {
            true -> {
                tv_toolbar_title.setTextColor(ContextCompat.getColorStateList(this@RecordAudioActivity, R.color.colorTurquoiseMedium))
                enableBackButton(false)
                enableActionSaveItem(false)
            }

            false -> {
                tv_toolbar_title.setTextColor(ContextCompat.getColorStateList(this@RecordAudioActivity, R.color.colorAccent))
                enableBackButton(true)
                enableActionSaveItem(true)
            }
        }
    }

    private fun setRecordButtonState() {
        var drawable: Drawable? = null
        var backgroundColor: ColorStateList? = null

        when(isRecordingAudio) {
            true -> {
                drawable = ContextCompat.getDrawable(this@RecordAudioActivity, R.drawable.ic_stop_accent_32dp)
                backgroundColor = ContextCompat.getColorStateList(this@RecordAudioActivity, android.R.color.white)
            }

            false -> {
                drawable = ContextCompat.getDrawable(this@RecordAudioActivity, R.drawable.ic_mic_white_32dp)
                backgroundColor = ContextCompat.getColorStateList(this@RecordAudioActivity, R.color.colorAccent)
            }
        }

        recordBtn.setImageDrawable(drawable)
        recordBtn.backgroundTintList = backgroundColor
    }

    override fun getHomeIcon(): Int = R.drawable.ic_close_white_24dp

    override fun getMenuLayout(): Int = R.menu.toolbar_ok_menu

    override fun onMenuAction(id_item: Int) {
        when(id_item) {
            R.id.action_ok -> {
                Toast.makeText(this, "OK!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(STATE_IS_RECORDING_AUDIO, isRecordingAudio)
    }

    override fun onResume() {
        super.onResume()

        if(isRecordingAudio) viewModel.startChronometer()
    }

    override fun onPause() {
        super.onPause()

        if(isRecordingAudio) viewModel.pauseChronometer()
    }
}

interface RecordAudioListener {
    fun onRecordButtonClicked(v: View)
}