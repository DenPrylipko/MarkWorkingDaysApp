package com.genius.markworkingdaysapp.ui.calendar

import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.MonthItemUiState
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialogUiState
import java.time.DayOfWeek

data class CalendarUiState(
    val displayedMonthItem: MonthItemUiState,
    val displayedMonthDailyRate: Int,
    val currencyLabel: String,
    val days: List<DayCellUiModel>,
    val daysOfWeek: List<DayOfWeek>,
    val monthStatistics: MonthStatistics,
    val yearMonthDialogState: YearMonthDialogUiState? = null
)