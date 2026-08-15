package com.genius.markworkingdaysapp.data.db

import com.genius.markworkingdaysapp.model.DayType
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

    suspend fun isDayChecked(date: LocalDate): Boolean {
        return dao.getByDay(date.toEpochDay()) != null
    }

    fun observeRangeMap(from: LocalDate, to: LocalDate): Flow<Map<Long, WorkDayEntity>> =
        dao.observeRange(from.toEpochDay(), to.toEpochDay())
            .map { list -> list.associateBy { it.epochDay } }

    fun observeWorkedEpochDays(): Flow<List<Long>> {
        return dao.observeWorkedEpochDays()
    }
}