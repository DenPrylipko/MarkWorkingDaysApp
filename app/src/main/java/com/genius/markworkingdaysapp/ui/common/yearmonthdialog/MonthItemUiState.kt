package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import com.genius.markworkingdaysapp.model.MonthStatus
import java.time.YearMonth

data class MonthItemUiState(
    val yearMonth: YearMonth,
    val status: MonthStatus
)

fun YearMonth.toMonthItemUiState(status: MonthStatus): MonthItemUiState {
    return MonthItemUiState(
        yearMonth = this,
        status = status,
    )
}