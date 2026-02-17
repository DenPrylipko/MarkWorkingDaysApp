package com.genius.markworkingdaysapp.ui.main.model

import java.time.DayOfWeek
import java.time.LocalDate

data class MainUiState(
    val now: LocalDate,
    val firstDayOfWeek: DayOfWeek,
    val monthDaysData: List<DayCell>,
    val weekdaysData: List<DayOfWeek>,
    val dailyRate: Int,
    val currency: String,
    val todayCheckedStatus: Boolean,
    val monthStats: MonthStats,
)

data class MonthStats(
    val workingDays: Int = 0,
    val totalBonuses: Int = 0,
    val totalEarned: Int = 0
)