package com.genius.markworkingdaysapp.ui.calendar

import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.ui.calendar.model.MonthGridUiModel
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.MonthItemUiState
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialogUiState
import java.time.DayOfWeek

data class CalendarUiState(
    val displayedMonthItem: MonthItemUiState,
    val displayedMonthDailyRate: Int,
    val currencyLabel: String,
    val monthGrid: MonthGridUiModel,
    val daysOfWeek: List<DayOfWeek>,
    val monthStatistics: MonthStatistics,
    val yearMonthDialogState: YearMonthDialogUiState? = null
)