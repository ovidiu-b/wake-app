package com.weikappinc.weikapp.adapters.main_activity

import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.databinding.AlarmListItemBinding
import com.weikappinc.weikapp.getLayoutBinding
import io.reactivex.subjects.PublishSubject

class AlarmListAdapter : RecyclerView.Adapter<AlarmItemViewHolder>() {

    val updatesLiveData: MutableLiveData<Alarm> = MutableLiveData()

    private var alarms: MutableList<Alarm>? = null

    val clickOnAlarmEvents: PublishSubject<String> = PublishSubject.create()

    fun updateListOfAlarms(alarms: MutableList<Alarm>) {
        this.alarms = alarms

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmItemViewHolder {
        val mBinding: AlarmListItemBinding = parent.getLayoutBinding(R.layout.alarm_list_item) as AlarmListItemBinding

        mBinding.alarmOnOff.setOnCheckedChangeListener { view, isChecked ->
            if(view.isPressed) {
                mBinding.alarm?.isActivated = isChecked

                updatesLiveData.value = mBinding.alarm
            }
        }

        return AlarmItemViewHolder(mBinding)
    }

    override fun getItemCount(): Int = alarms?.size ?: 0

    override fun onBindViewHolder(holder: AlarmItemViewHolder, position: Int){
        holder.itemView.setOnClickListener{
            clickOnAlarmEvents.onNext(alarms!![position].id)
        }

        holder.bind(alarms!![position])
        holder.mBinding.executePendingBindings()
    }
}

class AlarmItemViewHolder(val mBinding: AlarmListItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
    fun bind(alarm: Alarm) { mBinding.alarm = alarm }
}