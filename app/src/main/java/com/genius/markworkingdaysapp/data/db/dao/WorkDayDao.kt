package com.genius.markworkingdaysapp.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkDayDao {

    @Upsert
    suspend fun upsert(day: WorkDayEntity)

    @Upsert
    suspend fun upsertAll(days: List<WorkDayEntity>)

    @Query("SELECT * FROM work_days WHERE epochDay = :epochDay")
    suspend fun getByEpochDay(epochDay: Long): WorkDayEntity?

    @Query("SELECT * FROM work_days WHERE epochDay BETWEEN :fromDay AND :toDay ORDER BY epochDay ASC")
    suspend fun getRange(fromDay: Long, toDay: Long): List<WorkDayEntity>

    @Query("SELECT * FROM work_days WHERE epochDay BETWEEN :fromDay AND :toDay ORDER BY epochDay ASC")
    fun observeRange(fromDay: Long, toDay: Long): Flow<List<WorkDayEntity>>
}