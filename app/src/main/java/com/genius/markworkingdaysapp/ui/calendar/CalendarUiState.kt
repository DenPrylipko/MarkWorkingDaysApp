package com.genius.markworkingdaysapp.ui.calendar

import com.genius.markworkingdaysapp.common.buildMonthGridBase
import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialogUiState
import java.time.DayOfWeek
import java.time.YearMonth

data class CalendarUiState(
    val displayedMonth: YearMonth,
    val monthGrid: MonthGridUiModel,
    val daysOfWeek: List<DayOfWeek>,
    val monthStatistics: MonthStatistics,
    val yearMonthDialogState: YearMonthDialogUiState
)