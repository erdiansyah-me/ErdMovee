package com.greildev.core.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.greildev.core.data.source.local.entities.UserDataEntity

@Dao
interface UserDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(user: UserDataEntity)

    @Query("SELECT * FROM user_data_entity LIMIT 1")
    suspend fun getUserData(): UserDataEntity?

    @Delete
    fun deleteUserData(user: UserDataEntity)

    @Query("DELETE FROM user_data_entity")
    fun deleteAllUserData()

    @Update
    fun updateUserData(user: UserDataEntity)
}