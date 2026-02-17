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

    @Query("SELECT * FROM work_days WHERE epochDay = :day")
    suspend fun getByDay(day: Long): WorkDayEntity?

    @Query("DELETE FROM work_days WHERE epochDay = :day")
    suspend fun deleteByDay(day: Long)

    @Query("SELECT * FROM work_days WHERE epochDay BETWEEN :fromDay AND :toDay ORDER BY epochDay ASC")
    fun observeRange(fromDay: Long, toDay: Long): Flow<List<WorkDayEntity>>
}