package com.genius.markworkingdaysapp.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

data class MainUiState(
    val date: LocalDate,
    val rvData: RVData,
    val monthStats: MonthStats,
    val settingsDrawerState: SettingsDrawerState,
    val chooseMonthDialogState: ChooseMonthDialogState
)

data class RVData(
    val monthDaysData: List<DayCell>,
    val weekdaysData: List<DayOfWeek>,
    val monthItemsData: List<MonthItem>
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

data class ChooseMonthDialogState(
    val year: Year,
    val monthItems: List<MonthItem>
)

data class MainConfig(
    val date: LocalDate,
    val firstDOW: DayOfWeek,
    val currentMonth: YearMonth,
    val dailyRate: Int,
    val currency: String
)
