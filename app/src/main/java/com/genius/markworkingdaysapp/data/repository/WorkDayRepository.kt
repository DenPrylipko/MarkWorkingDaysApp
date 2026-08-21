package com.genius.markworkingdaysapp.data.repository

import androidx.room.withTransaction
import com.genius.markworkingdaysapp.data.db.AppDatabase
import com.genius.markworkingdaysapp.data.db.entity.MonthRateEntity
import com.genius.markworkingdaysapp.data.mapper.toEntity
import com.genius.markworkingdaysapp.data.mapper.toWorkDay
import com.genius.markworkingdaysapp.model.WorkDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth

class WorkDayRepository(
    private val database: AppDatabase
) {
    private val workDayDao = database.workDayDao()
    private val monthRateDao = database.monthRateDao()

    suspend fun saveDay(
        workDay: WorkDay,
        defaultDailyRate: Int
    ) {
        database.withTransaction {

            val monthStartEpochDay = workDay.date
                .withDayOfMonth(1)
                .toEpochDay()

            monthRateDao.insertIfAbsent(
                MonthRateEntity(
                    monthStartEpochDay = monthStartEpochDay,
                    dailyRate = defaultDailyRate
                )
            )

            workDayDao.upsert(workDay.toEntity())
        }
    }

    suspend fun setDailyRateForMonth(
        month: YearMonth,
        dailyRate: Int
    ) {

        database.withTransaction {
            val fromDay = month.atDay(1).toEpochDay()
            val toDay = month.atEndOfMonth().toEpochDay()

            monthRateDao.upsert(
                MonthRateEntity(
                    monthStartEpochDay = fromDay,
                    dailyRate = dailyRate
                )
            )

            val recalculatedDays = workDayDao
                .getRange(fromDay, toDay)
                .map { entity ->
                    entity.toWorkDay()
                        .withRecalculatedEarned(dailyRate)
                        .toEntity()
                }

            workDayDao.upsertAll(recalculatedDays)
        }
    }

    suspend fun getDailyRateForMonth(month: YearMonth): Int? {
        val monthStartEpochDay = month.atDay(1).toEpochDay()
        return monthRateDao
            .getByMonth(monthStartEpochDay)
            ?.dailyRate
    }

    suspend fun hasWorkDayEntry(date: LocalDate): Boolean =
        workDayDao.getByEpochDay(date.toEpochDay()) != null

    fun observeWorkDaysInRange(
        from: LocalDate,
        to: LocalDate
    ): Flow<Map<LocalDate, WorkDay>> =
        workDayDao
            .observeRange(
                from.toEpochDay(),
                to.toEpochDay()
            )
            .map { entities ->
                entities
                    .map { it.toWorkDay() }
                    .associateBy { it.date }
            }

}