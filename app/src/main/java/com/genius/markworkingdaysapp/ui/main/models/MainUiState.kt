package com.genius.markworkingdaysapp.ui.main.models

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class MainUiState(
    val now: LocalDate,
    val firstDayOfWeek: DayOfWeek,
    val currentMonth: YearMonth,
    val monthStats: MonthStats,

    val monthDaysData: List<DayCell>,
    val weekdaysData: List<DayOfWeek>,

    val todayCheckedStatus: Boolean,

    val dailyRate: Int,
    val currency: String,
)

data class MonthStats(
    val workingDays: Int = 0,
    val totalBonuses: Int = 0,
    val totalEarned: Int = 0
)

data class MainConfig(
    val now: LocalDate,
    val firstDOW: DayOfWeek,
    val currentMonth: YearMonth,

    val dailyRate: Int,
    val currency: String
)
