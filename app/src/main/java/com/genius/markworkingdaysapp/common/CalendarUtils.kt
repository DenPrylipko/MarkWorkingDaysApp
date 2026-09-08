package com.genius.markworkingdaysapp.common

import android.annotation.SuppressLint
import android.content.res.Resources
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.model.MonthStatus
import com.genius.markworkingdaysapp.model.WorkDay
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.MonthItemUiState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.mutableSetOf

private val dayMonthFormatter = DateTimeFormatter.ofPattern("dd.MM")

fun buildMonthGrid(
    yearMonth: YearMonth,
    firstDayOfWeek: DayOfWeek,
    workDays: Map<LocalDate, WorkDay>
): List<DayCellUiModel> {
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

    return listOfDays
}

fun buildWeekdays(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
    return (0 until 7).map { shift ->
        firstDayOfWeek.plus(shift.toLong())
    }
}


fun buildMonthItemsForYear(
    year: Year,
    workDays: Map<LocalDate, WorkDay>
): List<MonthItemUiState> {

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

        MonthItemUiState(
            yearMonth = monthDate,
            status = status
        )
    }
}

fun buildCalendarShareText(
    days: List<DayCellUiModel>,
    currencyLabel: String,
    statistics: MonthStatistics,
    resources: Resources,
): String {
    return buildString {
        days
            .mapNotNull { it.workDay }
            .forEach { workDay ->
                if (workDay.status == DayStatus.NOT_WORKED) return@forEach
                appendLine(
                    formatDay(
                        workDay = workDay,
                        currencyLabel = currencyLabel
                    )
                )
            }

        appendLine()
        appendLine(resources.getString(R.string.share_text_working_days, statistics.workedDays))
        appendLine(resources.getString(R.string.share_text_bonuses, statistics.totalBonuses, currencyLabel))
        append(resources.getString(R.string.share_text_total_earned, statistics.totalEarned, currencyLabel))
    }
}

fun formatDay(
    workDay: WorkDay,
    currencyLabel: String
): String = buildString {
    append(workDay.date.format(dayMonthFormatter))

    workDay.bonus?.let { bonus ->
        append(" +")
        append(bonus)
        append(" ")
        append(currencyLabel)
    }

    if (workDay.status == DayStatus.SHORT_DAY) {
        append(" ")
        append(workDay.earned)
        append(" ")
        append(currencyLabel)
    }

    workDay.note
        ?.takeIf { it.isNotBlank() }
        ?.let { note ->
            append(" ")
            append("\"$note\"")
        }
}

fun YearMonth.getMonthTitle(
    withYear: Boolean = false
): String {
    val locale = Locale.getDefault()
    val formatter = DateTimeFormatter.ofPattern("LLLL", locale)

    return this
        .format(formatter)
        .replaceFirstChar { it.uppercase(locale) } + if (withYear) " ${this.year}" else ""

}

@SuppressLint("DefaultLocale")
fun formatTime(hour: Int, minute: Int): String {
    return String.format( "%02d:%02d", hour, minute)
}