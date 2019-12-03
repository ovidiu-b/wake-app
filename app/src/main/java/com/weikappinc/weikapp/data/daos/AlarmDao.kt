package com.weikappinc.weikapp.data.daos

import androidx.room.*
import com.weikappinc.weikapp.data.entities.Alarm
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
abstract class AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg alarms: Alarm)

    @Update
    abstract fun update(vararg alarms: Alarm)

    @Delete
    abstract fun delete(vararg alarms: Alarm)

    @Query("SELECT * FROM Alarm WHERE id=:id")
    protected abstract fun getDistinctFlowableAlarmById(id: String) : Flowable<Alarm>

    @Query("SELECT * FROM Alarm WHERE id=:id")
    abstract fun getAlarmById(id: String) : Alarm

    @Query("SELECT * FROM Alarm WHERE id=:id")
    abstract fun getMaybeAlarmById(id: String) : Maybe<Alarm>

    @Query("SELECT * FROM Alarm ORDER BY isActivated desc, hour, minutes")
    protected abstract fun getDistinctFlowableAlarms(): Flowable<MutableList<Alarm>>

    fun getFlowableAlarmById(id: String) : Flowable<Alarm> = getDistinctFlowableAlarmById(id).distinctUntilChanged()

    fun getFlowableAlarms(): Flowable<MutableList<Alarm>> = getDistinctFlowableAlarms().distinctUntilChanged()
}
