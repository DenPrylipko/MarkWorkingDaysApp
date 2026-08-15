package com.genius.markworkingdaysapp.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDayDao {

    @Upsert
    suspend fun upsert(day: WorkDayEntity)

    @Query("SELECT * FROM work_days WHERE epochDay = :day")
    suspend fun getByDay(day: Long): WorkDayEntity?

    @Query("DELETE FROM work_days WHERE epochDay = :day")
    suspend fun deleteByDay(day: Long)

    @Query("SELECT * FROM work_days WHERE epochDay BETWEEN :fromDay AND :toDay ORDER BY epochDay ASC")
    fun observeRange(fromDay: Long, toDay: Long): Flow<List<WorkDayEntity>>

    @Query("SELECT epochDay FROM work_days WHERE worked = 1")
    fun observeWorkedEpochDays(): Flow<List<Long>>
}