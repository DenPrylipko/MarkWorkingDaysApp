package com.genius.markworkingdaysapp.ui.statistics

import java.time.YearMonth

class StatisticsUiState(
    val lastMonthOnChart: YearMonth,
    val chartPeriod: ChartPeriod,
    val monthIncomes: List<MonthIncomeUiModel> = emptyList(),
    val currencyLabel: String = "",
    val isLoading: Boolean = true
) {

    val totalIncome: Long
        get() = monthIncomes.sumOf { it.income }

    val highestIncome: Long
        get() = monthIncomes.maxOfOrNull { it.income } ?: 0L

    val lowestIncome: Long
        get() = monthIncomes.minOfOrNull { it.income } ?: 0L

    val averageIncome: Double
        get() = if (monthIncomes.isEmpty()) {
            0.0
        } else {
            totalIncome.toDouble() / monthIncomes.size
        }

}