package com.genius.markworkingdaysapp

import com.genius.markworkingdaysapp.ui.main.models.DayCell
import com.genius.markworkingdaysapp.ui.main.models.MonthGridBase
import com.genius.markworkingdaysapp.ui.main.models.MonthItem
import com.genius.markworkingdaysapp.ui.main.models.MonthStatus
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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

fun buildMonthItemsForYear(
    year: Int,
    locale: Locale = Locale.getDefault(),
    now: YearMonth = YearMonth.now()
): List<MonthItem> {
    return Month.entries.map { month ->
        val ym = YearMonth.of(year, month)

        val status = when {
            ym.isBefore(now) -> MonthStatus.PAST
            ym == now -> MonthStatus.CURRENT
            else -> MonthStatus.FUTURE
        }

        MonthItem(
            yearMonth = ym,
            title = month.getDisplayName(TextStyle.FULL, locale)
                .replaceFirstChar { it.titlecase(locale) },
            status = status
        )
    }
}

fun getMonthTitle(yearMonth: YearMonth): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("LLLL", locale)
    return yearMonth.format(formatter).replaceFirstChar { it.uppercase(locale) }
}