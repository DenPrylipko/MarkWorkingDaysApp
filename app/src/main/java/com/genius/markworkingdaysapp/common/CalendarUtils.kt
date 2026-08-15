package com.genius.markworkingdaysapp.common

import android.annotation.SuppressLint
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.MonthGridBase
import com.genius.markworkingdaysapp.model.MonthItem
import com.genius.markworkingdaysapp.model.MonthStatus
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun buildMonthGridBase(
    yearMonth: YearMonth,
    firstDayOfWeek: DayOfWeek,
): MonthGridBase {
    val firstDayOfMonth = yearMonth.atDay(1)

    val shift = (firstDayOfMonth.dayOfWeek.value - firstDayOfWeek.value + 7) % 7

    val start = firstDayOfMonth.minusDays(shift.toLong())

    val listOfDays = (0 until 42).map {i ->
        val day = start.plusDays(i.toLong())
        DayCell(
            date = day,
            isInCurrentMonth = (day.monthValue == yearMonth.monthValue)
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
    year: Year,
    workedMonth: Set<YearMonth>,
    locale: Locale = Locale.getDefault()
): List<MonthItem> {
    val today = LocalDate.now()

    return (1..12).map { monthValue ->
        val monthDate = YearMonth.of(year.value, monthValue)
        val isWorked = monthDate in workedMonth

        val status = when {

            monthDate.year == today.year && monthDate.monthValue == today.monthValue ->  {
                MonthStatus.CURRENT
            }
            monthDate.isBefore(YearMonth.from(today)) && isWorked -> {
                MonthStatus.PAST_WORKED
            }
            monthDate.isBefore(YearMonth.from(today)) && !isWorked -> {
                MonthStatus.PAST_NOT_WORKED
            }
            else -> MonthStatus.FUTURE
        }

        MonthItem(
            yearMonth = monthDate,
            title = monthDate.month.getDisplayName(TextStyle.FULL, locale)
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

@SuppressLint("DefaultLocale")
fun formatTime(hour: Int, minute: Int): String {
    return String.format( "%02d:%02d", hour, minute)
}