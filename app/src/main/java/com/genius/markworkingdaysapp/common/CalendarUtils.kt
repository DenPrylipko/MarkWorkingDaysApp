package com.genius.markworkingdaysapp.common

import android.annotation.SuppressLint
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.model.MonthStatus
import com.genius.markworkingdaysapp.model.WorkDay
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.calendar.model.MonthGridUiModel
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.MonthItem
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.mutableSetOf

fun buildMonthGrid(
    yearMonth: YearMonth,
    firstDayOfWeek: DayOfWeek,
    workDays: Map<LocalDate, WorkDay>
): MonthGridUiModel {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    val shift = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    val start = firstDayOfMonth.minusDays(shift.toLong())

    val listOfDays = (0 until 42).map {i ->
        val day = start.plusDays(i.toLong())
        DayCellUiModel(
            date = day,
            workDay = if (day in firstDayOfMonth..lastDayOfMonth) {
                workDays[day]
            } else {
                null
            }
        )
    }

    return MonthGridUiModel(listOfDays)
}

fun buildWeekdays(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
    return (0 until 7).map { shift ->
        firstDayOfWeek.plus(shift.toLong())
    }
}

fun buildMonthItemsForYear(
    year: Year,
    workDays: Map<LocalDate, WorkDay>
): List<MonthItem> {

    val monthsWithWorkDays = workDays.values
        .filter { workDay ->
            workDay.status == DayStatus.FULL_DAY ||
                    workDay.status == DayStatus.SHORT_DAY
        }
        .mapTo(mutableSetOf()) { workDay ->
            YearMonth.from(workDay.date)
        }

    val today = YearMonth.now()

    return (1..12).map { monthValue ->
        val monthDate = YearMonth.of(year.value, monthValue)

        val status = when {

            monthDate == today ->
                MonthStatus.CURRENT

            monthDate > today -> {
                MonthStatus.FUTURE
            }
            monthDate in monthsWithWorkDays -> {
                MonthStatus.PAST_WORKED
            }
            else -> MonthStatus.PAST_NOT_WORKED
        }

        MonthItem(
            yearMonth = monthDate,
            status = status
        )
    }
}

fun getMonthTitle(yearMonth: YearMonth): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("LLLL", locale)

    return yearMonth
        .format(formatter)
        .replaceFirstChar { it.uppercase(locale) }
}

@SuppressLint("DefaultLocale")
fun formatTime(hour: Int, minute: Int): String {
    return String.format( "%02d:%02d", hour, minute)
}