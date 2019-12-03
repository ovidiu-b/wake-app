package com.weikappinc.weikapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weikappinc.weikapp.data.daos.AlarmDao
import com.weikappinc.weikapp.data.daos.CurrentUserDataDao
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.data.entities.CurrentUserData

const val DATABASE_NAME = "WeikAppDB"

@Database(entities = [Alarm::class, CurrentUserData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao() : AlarmDao
    abstract fun currentUserData() : CurrentUserDataDao
}