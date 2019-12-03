package com.weikappinc.weikapp.presenters

import android.annotation.SuppressLint
import android.util.Log
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.eventbus.events.single_alarm_activity.*
import com.weikappinc.weikapp.repositories.AlarmRepository
import com.weikappinc.weikapp.utils.CalendarUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SingleAlarmPresenter"

class SingleAlarmPresenter(private val alarmRepository: AlarmRepository) {
    private val dayNumbers: MutableList<Int> = mutableListOf()

    lateinit var alarm: Alarm

    private val disposables = CompositeDisposable()

    fun loadAlarm(id: String?) {
        if(!isAlarmInitialized()) {
            if(id == null){
                alarm = Alarm.getNewInstance()

                dayNumbers.add(CalendarUtil.getCurrentDay())

                notifyActivity()
            } else {
                disposables.add(
                    alarmRepository
                        .getMaybeAlarmById(id)
                        .subscribeOn(Schedulers.io())
                        .subscribe{
                            alarm = it

                            // Convertimos la cadena de caracteres en un entero y los introducimos en dayNumbers cada uno
                            parseFrequencyStrToList(alarm.alarmFrequency).map { numberStr -> dayNumbers.add(numberStr.trim().toInt()) }

                            notifyActivity()
                        }
                )
            }
        }
    }

    fun isAlarmInitialized() = ::alarm.isInitialized

    private fun notifyActivity() {
        EventBus.getDefault().post(AlarmTimeEvent(alarm.hour, alarm.minutes))
        EventBus.getDefault().post(AlarmTimeRemainingEvent(getWhenAlarmWillPlay()))
        EventBus.getDefault().post(AlarmDescriptionEvent(alarm.description))
        EventBus.getDefault().post(AlarmDayEvent(dayNumbers))
        EventBus.getDefault().post(AlarmAudioEvent(alarm.audioPath))
        EventBus.getDefault().post(AlarmVibrantEvent(alarm.isVibrant))
    }

    @SuppressLint("SimpleDateFormat")
    private fun getWhenAlarmWillPlay(): String {
        val str = "La alarma sonará en "

        val currentDay = CalendarUtil.getCurrentDay()
        val alarmDay: Int

        /*if(dayNumbers.indexOf(currentDay) != -1) {
            alarmDay = currentDay
        }
        else {
            var tempCurrentDay: Int = currentDay

            while (dayNumbers.indexOf(tempCurrentDay) == -1) {
                if(tempCurrentDay == 7) tempCurrentDay = 1 else tempCurrentDay++

                if(alarm.hour <= CalendarUtil.getCurrentHour() && alarm.minutes < CalendarUtil.getCurrentMinutes()){
                    tempCurrentDay++
                }
            }

            alarmDay = tempCurrentDay
        }*/

        var tempCurrentDay: Int = currentDay

        if(alarm.hour <= CalendarUtil.getCurrentHour() && alarm.minutes < CalendarUtil.getCurrentMinutes() && dayNumbers.indexOf(tempCurrentDay) != -1){
            tempCurrentDay++
        }

        while (dayNumbers.indexOf(tempCurrentDay) == -1) {
            if(tempCurrentDay == 7) tempCurrentDay = 1 else tempCurrentDay++
        }

        alarmDay = tempCurrentDay

        /*
            La próxima fecha en la que la alarma sonará
            Será aquella fecha que se inmediatamente superior a la fecha actual
            Tenemos que encontrar el día que esté más próximo al día actual
        */
        val d1 = Calendar.getInstance()

        /*d1.set(Calendar.HOUR_OF_DAY, alarm.hour)
        d1.set(Calendar.MINUTE, alarm.minutes)
        dayNumbers.min()?.let{
            d1.set(Calendar.DAY_OF_WEEK, it)
        }*/

        val d2 = Calendar.getInstance()
        d2.set(Calendar.HOUR_OF_DAY, CalendarUtil.getCurrentHour())
        d2.set(Calendar.MINUTE, CalendarUtil.getCurrentMinutes())
        d2.set(Calendar.DAY_OF_WEEK, currentDay)

        val daysPassed = when {
            alarmDay > currentDay -> alarmDay - currentDay
            alarmDay < currentDay -> 7 - (currentDay - alarmDay)
            else -> if(dayNumbers.indexOf(currentDay) != -1 && alarm.hour <= CalendarUtil.getCurrentHour() && alarm.minutes < CalendarUtil.getCurrentMinutes()) 7 else 0
        }

        d1.set(Calendar.DAY_OF_WEEK, currentDay)

        if(daysPassed != 0) d1.add(Calendar.DAY_OF_MONTH, daysPassed)

        d1.set(Calendar.HOUR_OF_DAY, alarm.hour)
        d1.set(Calendar.MINUTE, alarm.minutes)

        val diff: Long

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")

        Log.d("SingleAlarmPresenter", "d1: ${sdf.format(d1.time)}")
        Log.d("SingleAlarmPresenter", "d2: ${sdf.format(d2.time)}")

        /*Log.d("SingleAlarmPresenter", "d1: $d1")
        Log.d("SingleAlarmPresenter", "d2: $d2")*/

        var hoursRemaining = 0
        var minutesRemaining = 0

        if(d1.timeInMillis > d2.timeInMillis) {
            diff = d1.timeInMillis - d2.timeInMillis

            hoursRemaining = (diff / (60 * 60 * 1000)).toInt()
            minutesRemaining = (diff / (60 * 1000) % 60).toInt()

        } else if(d2.timeInMillis > d1.timeInMillis) {
            diff = d2.timeInMillis - d1.timeInMillis

            hoursRemaining = 23 - (diff / (60 * 60 * 1000)).toInt()
            minutesRemaining = 59 - (diff / (60 * 1000) % 60).toInt()
        }

        val daysRemaining = hoursRemaining / 24

        hoursRemaining %= 24

        /*if(hoursRemaining == 0 && minutesRemaining == 0) {
            return "${str}23 horas 59 minutos"
        }*/

        return "$str${getDayStr(daysRemaining)}${getHourStr(hoursRemaining)}${getMinuteStr(minutesRemaining)}".trim()
    }

    private fun getDayStr(days: Int): String {
        return if(days == 1) {
            "$days día "
        } else {
            if(days == 0) return ""

            "$days días "
        }
    }

    private fun getHourStr(hours: Int): String {
        return if(hours == 1) {
            "$hours hora "
        } else {
            if(hours == 0) return ""

            "$hours horas "
        }
    }

    private fun getMinuteStr(minutes: Int): String {
        return if(minutes == 1) {
            "$minutes minuto"
        } else {
            if(minutes == 0) return ""

            "$minutes minutos"
        }
    }

    fun setAlarmDescription(description: String) {
        if(isAlarmInitialized()) alarm.description = description
    }

    fun setAudioPath(audioPath: String) {
        if(isAlarmInitialized()) alarm.audioPath = audioPath
    }

    fun setAlarmHour(hour: Int) {
        if(hour in 0..23) {
            if(isAlarmInitialized()) alarm.hour = hour
        } else {
            Log.e(TAG, "No puede asignar la hora $hour: debe estar entre 0 y 23")
        }

        EventBus.getDefault().post(AlarmTimeRemainingEvent(getWhenAlarmWillPlay()))
    }

    fun setAlarmMinutes(minutes: Int) {
        if(minutes in 0..59) {
            if(isAlarmInitialized()) alarm.minutes = minutes
        } else {
            Log.e(TAG, "No puede asignar los minutos $minutes: debe estar entre 0 y 59")
        }

        EventBus.getDefault().post(AlarmTimeRemainingEvent(getWhenAlarmWillPlay()))
    }

    fun addDay(dayNumber: Int) {
        if(dayNumbers.indexOf(dayNumber) == -1) {
            dayNumbers.add(dayNumber)

            EventBus.getDefault().post(AlarmDayEvent(dayNumbers))
            EventBus.getDefault().post(AlarmTimeRemainingEvent(getWhenAlarmWillPlay()))
        }
    }

    fun removeDay(dayNumber: Int) {
        if(dayNumbers.indexOf(dayNumber) != -1) {
            dayNumbers.remove(dayNumber)

            if(dayNumbers.isEmpty()) {
                dayNumbers.add(CalendarUtil.getCurrentDay())
            }

            EventBus.getDefault().post(AlarmDayEvent(dayNumbers))
            EventBus.getDefault().post(AlarmTimeRemainingEvent(getWhenAlarmWillPlay()))
        }
    }

    fun saveAlarm() {
        if(alarm.description.isEmpty()) {
            EventBus.getDefault().post(AlarmErrorEvent("Necesita asignar un título a la alarma"))

            return
        }

        if(dayNumbers.isEmpty()) {
            EventBus.getDefault().post(AlarmErrorEvent("Necesita asignar al menos un día de la semana"))

            return
        }

        if(!File(alarm.audioPath).exists()) {
            EventBus.getDefault().post(AlarmErrorEvent("Necesita asignar un audio a la alarma"))

            return
        }

        // Convertimos el array de numeros en una cadena de caracteres separados por una coma y un espacio

        alarm.alarmFrequency = dayNumbers.distinct().joinToString { "$it" }

        alarm.isActivated = true

        alarmRepository.insertAlarms(alarm)

        EventBus.getDefault().post(AlarmSaveEvent(true))
    }

    fun onDispose() {
        disposables.clear()
    }

    companion object {
        fun parseFrequencyStrToList(frequencyStr: String): List<String> = frequencyStr.split(",")
    }
}
