package com.genius.markworkingdaysapp.ui.main.models

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

data class MainUiState(
    val now: LocalDate,
    val currentMonth: YearMonth,
    val currentYear: Year,
    val monthStats: MonthStats,

    val monthDaysData: List<DayCell>,
    val weekdaysData: List<DayOfWeek>,
    val monthItems: List<MonthItem>,

    val settingsDrawerState: SettingsDrawerState
)

data class MonthStats(
    val workingDays: Int = 0,
    val totalBonuses: Int = 0,
    val totalEarned: Int = 0
)

data class SettingsDrawerState(
    val dailyRate: Int,
    val currency: String,
    val firstDayOfWeek: DayOfWeek,
    val notificationsEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val todayChecked: Boolean
)

data class DateChooseDialogState(
    val currentYear: Year,
    val monthItems: List<MonthItem>
)

data class MainConfig(
    val shownDate: LocalDate,
    val firstDOW: DayOfWeek,
    val currentMonth: YearMonth,
    val dailyRate: Int,
    val currency: String
)
