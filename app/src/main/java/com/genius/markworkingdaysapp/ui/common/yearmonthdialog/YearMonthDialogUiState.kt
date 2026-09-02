package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import java.time.Year

data class YearMonthDialogUiState(
    val displayedYear: Year,
    val monthItems: List<MonthItemUiState> = emptyList(),
    val isLoading: Boolean = true,
)