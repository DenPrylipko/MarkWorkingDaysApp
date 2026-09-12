package com.genius.markworkingdaysapp.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.data.repository.SettingsRepository
import com.genius.markworkingdaysapp.data.repository.WorkDayRepository
import com.genius.markworkingdaysapp.model.WorkDay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth

class StatisticsViewModel(
    private val settingsRepository: SettingsRepository,
    private val workDayRepository: WorkDayRepository,
) : ViewModel() {

    private val _lastMonthOnChart = MutableStateFlow(YearMonth.now())

    private val _chartPeriod = MutableStateFlow(ChartPeriod.SIX_MONTHS)


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatisticsUiState> =
        combine(
            _lastMonthOnChart,
            _chartPeriod,
        ) { lastMonth, chartPeriod ->
            lastMonth to chartPeriod
        }.flatMapLatest { (lastMonth, chartPeriod) ->

            val firstMonth = lastMonth.minusMonths(
                (chartPeriod.monthCount - 1).toLong()
            )

            val startDate = firstMonth.atDay(1)
            val endDate = lastMonth.atEndOfMonth()

            workDayRepository.observeWorkDaysInRange(
                from = startDate,
                to = endDate
            ).combine(settingsRepository.settings) { workDays, settings ->
                createUiState(
                    lastMonth = lastMonth,
                    chartPeriod = chartPeriod,
                    workDays = workDays,
                    currencyLabel = settings.currencyLabel,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState(
                lastMonthOnChart = _lastMonthOnChart.value,
                chartPeriod = _chartPeriod.value,
                isLoading = true
            ),
        )


    private fun createUiState(
        lastMonth: YearMonth,
        chartPeriod: ChartPeriod,
        workDays: Map<LocalDate, WorkDay>,
        currencyLabel: String,
    ): StatisticsUiState {

        val firstMonth = lastMonth.minusMonths(
            (chartPeriod.monthCount - 1).toLong()
        )

        val workDaysByMonth = workDays.entries.groupBy { entry ->
            YearMonth.from(entry.key)
        }

        val monthIncomes = List(chartPeriod.monthCount) { index ->
            val month = firstMonth.plusMonths(index.toLong())

            val income = workDaysByMonth[month]
                ?.sumOf { entry -> entry.value.earned.toLong() }
                ?: 0L

            MonthIncomeUiModel(
                month = month,
                income = income,
            )
        }

        return StatisticsUiState(
            lastMonthOnChart = lastMonth,
            chartPeriod = chartPeriod,
            monthIncomes = monthIncomes,
            currencyLabel = currencyLabel,
            isLoading = false,
        )
    }


    fun onLastMonthSelected(month: YearMonth) {
        _lastMonthOnChart.value = month
    }

    fun onChartPeriodSelected(period: ChartPeriod) {
        _chartPeriod.value = period
    }
}