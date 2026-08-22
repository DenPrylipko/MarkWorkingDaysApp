package com.genius.markworkingdaysapp.ui.calendar.model

import com.genius.markworkingdaysapp.model.WorkDay
import java.time.LocalDate
import java.time.YearMonth

data class DayCellUiModel(
    val date: LocalDate,
    val workDay: WorkDay?,
) {

    val hasEntry: Boolean
        get() = workDay != null

    fun isInDisplayedMonth(displayedMonth: YearMonth): Boolean {
        return YearMonth.from(date) == displayedMonth
    }

    fun isToday(today: LocalDate): Boolean {
        return date == today
    }
}