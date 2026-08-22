package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import java.time.Year

data class YearMonthDialogUiState(
    val year: Year,
    val monthItems: List<MonthItem>
)