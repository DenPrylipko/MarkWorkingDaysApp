package com.genius.markworkingdaysapp.data.db

import com.genius.markworkingdaysapp.ui.main.models.DayType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WorkDayRepository(private val dao: WorkDayDao) {

    suspend fun saveDay(date: LocalDate, dayType: DayType, bonus: Int?, earned: Int?, note: String?) {
        dao.upsert(
            WorkDayEntity(
                epochDay = date.toEpochDay(),
                worked = dayType != DayType.NOT_WORKED,
                bonus = bonus,
                shortDayEarned = if (dayType == DayType.SHORT) earned else null,
                note = note
            )
        )
    }

    suspend fun getDay(date: LocalDate) = dao.getByDay(date.toEpochDay())

    fun observeRange(from: LocalDate, to: LocalDate): Flow<Map<Long, WorkDayEntity>> =
        dao.observeRange(from.toEpochDay(), to.toEpochDay())
            .map { list -> list.associateBy { it.epochDay } }

}