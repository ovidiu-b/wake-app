package com.weikappinc.weikapp.adapters.audio_chooser_activity

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.databinding.AudioListItemBinding
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.AudioCheckedEvent
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.AudioPlayEvent
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.AudioStopEvent
import com.weikappinc.weikapp.getLayoutBinding
import org.greenrobot.eventbus.EventBus

class AudioListAdapter : RecyclerView.Adapter<AudioItemViewHolder>() {
    private var audios: MutableList<AudioItemPreview>? = null

    fun updateListOfAudios(audios: MutableList<AudioItemPreview>) {
        this.audios = audios

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioItemViewHolder {
        val mBinding: AudioListItemBinding = parent.getLayoutBinding(R.layout.audio_list_item) as AudioListItemBinding

        return AudioItemViewHolder(mBinding)
    }

    override fun getItemCount(): Int = audios?.size ?: 0

    override fun onBindViewHolder(holder: AudioItemViewHolder, position: Int) {
        holder.mBinding.playAudio.setOnClickListener{
            EventBus.getDefault().post(AudioPlayEvent(audios!![position]))
        }

        holder.mBinding.stopAudio.setOnClickListener{
            EventBus.getDefault().post(AudioStopEvent())
        }

        holder.mBinding.radioButton.setOnClickListener {
            EventBus.getDefault().post(AudioCheckedEvent(audios!![position]))
        }

        holder.bind(audios!![position])

        holder.mBinding.executePendingBindings()
    }
}

class AudioItemViewHolder(val mBinding: AudioListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(audio: AudioItemPreview) { mBinding.audio = audio }
}
