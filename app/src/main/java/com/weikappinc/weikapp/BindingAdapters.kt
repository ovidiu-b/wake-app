package com.weikappinc.weikapp

import android.annotation.SuppressLint
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.presenters.SingleAlarmPresenter

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) VISIBLE else GONE
}

@BindingAdapter("visible")
fun View.setVisible(show: Boolean) {
    visibility = if (show) VISIBLE else INVISIBLE
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(imageUrl: String?) {
    Picasso.get().isLoggingEnabled = true
    Picasso.get().load(imageUrl).fit().centerCrop().into(this)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("alarmTime")
fun setAlarmTime(textView: TextView, alarm: Alarm) {
    val hour = String.format("%02d", alarm.hour)
    val minutes = String.format("%02d", alarm.minutes)

    textView.text = "$hour:$minutes"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("alarmFrequency")
fun setAlarmFrequency(textView: TextView, frequencyStr: String) {
    val dayNumbers: MutableList<Int> = mutableListOf()

    SingleAlarmPresenter.parseFrequencyStrToList(frequencyStr).map { numberStr -> dayNumbers.add(numberStr.trim().toInt()) }

    dayNumbers.sort()

    val dayNames : MutableList<String> = mutableListOf()

    for (dayNumber in dayNumbers) {
        when(dayNumber) {
            1 -> dayNames.add("Lun")
            2 -> dayNames.add("Mar")
            3 -> dayNames.add("Mie")
            4 -> dayNames.add("Jue")
            5 -> dayNames.add("Vie")
            6 -> dayNames.add("Sab")
            7 -> dayNames.add("Dom")
        }
    }

    textView.text = if(dayNames.size == 7) "Todos los días"
                    else if(dayNames.size == 5 && dayNumbers.reduce{sum, element -> sum + element} == 15) "De lunes a viernes"
                    else if(dayNames.size == 1) getFullNameOfDayWeek(dayNumbers[0])
                    else if(dayNames.size == 2 && dayNumbers.reduce{sum, element -> sum + element} == 13) "Fines de semana"
                    else dayNames.joinToString { it }
}

private fun getFullNameOfDayWeek(day: Int) : String {
    return when(day) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        else -> "Domingo"
    }
}

//@BindingAdapter("title")
//fun setTitleRecordAudioActivity(textView: TextView, title: String?) {
//    textView.text = title
//}