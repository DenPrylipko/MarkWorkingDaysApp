package com.genius.markworkingdaysapp.data.repository

import com.genius.markworkingdaysapp.data.db.dao.WorkDayDao
import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WorkDayRepository(private val dao: WorkDayDao) {

    suspend fun saveDay(date: LocalDate, worked: Boolean, bonus: Int?, note: String?) {
        dao.upsert(
            WorkDayEntity(
                date.toEpochDay(),
                worked,
                bonus,
                note
            )
        )
    }

    suspend fun getDay(date: LocalDate) = dao.getByDay(date.toEpochDay())

    fun observeRange(from: LocalDate, to: LocalDate): Flow<Map<Long, WorkDayEntity>> =
        dao.observeRange(from.toEpochDay(), to.toEpochDay())
            .map { list -> list.associateBy { it.epochDay } }

}