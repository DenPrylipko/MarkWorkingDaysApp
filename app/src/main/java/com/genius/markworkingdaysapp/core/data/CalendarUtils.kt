package com.genius.markworkingdaysapp.core.data

import com.genius.markworkingdaysapp.ui.main.model.DayCell
import com.genius.markworkingdaysapp.ui.main.model.MonthGridBase
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

fun buildMonthGridBase(
    year: Int,
    month: Int,
    firstDayOfWeek: DayOfWeek,
): MonthGridBase {
    val ym = YearMonth.of(year, month)
    val firstDayOfMonth = ym.atDay(1)

    val shift = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    val start = firstDayOfMonth.minusDays(shift.toLong())

    val listOfDays = (0 until 42).map {i ->
        val day = start.plusDays(i.toLong())
        DayCell(
            date = day,
            isInCurrentMonth = (day.year == year && day.monthValue == month)
        )
    }

    val end = listOfDays.last().date

    return MonthGridBase(listOfDays, start, end)
}

fun buildWeekdays(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
    return (0 until 7).map { shift ->
        firstDayOfWeek.plus(shift.toLong())
    }
}

fun getMonthTitle(date: LocalDate): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("LLLL", locale)
    return date.format(formatter).replaceFirstChar { it.uppercase(locale) }
}