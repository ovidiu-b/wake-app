package com.weikappinc.weikapp.utils

import java.util.*

object CalendarUtil {
    fun getCurrentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    fun getCurrentMinutes(): Int = Calendar.getInstance().get(Calendar.MINUTE)
    fun getCurrentDay(): Int {
        return when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> 7
            2 -> 1
            3 -> 2
            4 -> 3
            5 -> 4
            6 -> 5
            else -> 6
        }
    }
}