package com.genius.markworkingdaysapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.genius.markworkingdaysapp.data.db.entity.MonthRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthRateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(monthRate: MonthRateEntity)

    @Upsert
    suspend fun upsert(monthRate: MonthRateEntity)

    @Query("SELECT * FROM `month_rates` WHERE monthStartEpochDay = :monthStartEpochDay")
    suspend fun getByMonth(monthStartEpochDay: Long): MonthRateEntity?

    @Query("SELECT `dailyRate` FROM `month_rates` WHERE monthStartEpochDay = :monthStartEpochDay")
    fun observeDailyRateByMonth(monthStartEpochDay: Long): Flow<Int?>

}