package com.weikappinc.weikapp.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weikappinc.weikapp.data.entities.CurrentUserData

@Dao
interface CurrentUserDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(currentUserData: CurrentUserData)

    @Query("DELETE FROM CurrentUserData")
    fun logout()

    @Query("SELECT * FROM CurrentUserData WHERE id='1'")
    fun getCurrentUserData() : CurrentUserData

    @Query("SELECT id FROM CurrentUserData WHERE id='1'")
    fun getUserId() : String

    @Query("SELECT email FROM CurrentUserData WHERE id='1'")
    fun getUserEmail() : String

    @Query("SELECT name FROM CurrentUserData WHERE id='1'")
    fun getUserName() : String
}