package com.weikappinc.weikapp.repositories

import com.weikappinc.weikapp.data.daos.AlarmDao
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.data.ioThread

class AlarmRepository (private val alarmDao: AlarmDao) {

    fun getFlowableAlarms() = alarmDao.getFlowableAlarms()

    fun getFlowableAlarmById(id: String) = alarmDao.getFlowableAlarmById(id)

    fun getMaybeAlarmById(id: String) = alarmDao.getMaybeAlarmById(id)

    fun getAlarmById(id: String) {
        ioThread {
            alarmDao.getAlarmById(id)
        }
    }

    fun updateAlarms(vararg alarms: Alarm) {
        ioThread {
            alarmDao.update(*alarms)
        }
    }

    fun insertAlarms(vararg alarms: Alarm) {
        ioThread {
            alarmDao.insert(*alarms)
        }
    }
}