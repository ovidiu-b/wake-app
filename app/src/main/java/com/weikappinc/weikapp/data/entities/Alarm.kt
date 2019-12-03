package com.weikappinc.weikapp.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.weikappinc.weikapp.utils.CalendarUtil
import java.util.*

@Entity
class Alarm(
    @PrimaryKey
    val id: String,
    var hour: Int,
    var minutes: Int,
    var description: String,
    var alarmFrequency: String,
    var isActivated: Boolean,
    var isVibrant: Boolean,
    var audioPath: String,
    val createdBy: String) : EntityBase() {

    @Ignore
    constructor(hour: Int,
                minutes: Int,
                description: String,
                alarmFrequency: String,
                isActivated: Boolean,
                isVibrant: Boolean,
                audioPath: String,
                createdBy: String) : this(
                                        UUID.randomUUID().toString(),
                                        hour,
                                        minutes,
                                        description,
                                        alarmFrequency,
                                        isActivated,
                                        isVibrant,
                                        audioPath,
                                        createdBy
                                    )
    {
        createdAt = System.currentTimeMillis()
    }

    companion object {
        @Ignore
        fun getNewInstance(): Alarm {
            return Alarm(
                CalendarUtil.getCurrentHour(),
                CalendarUtil.getCurrentMinutes() + 1,
                "",
                "",
                true,
                true,
                "",
                "12")
        }
    }

}
