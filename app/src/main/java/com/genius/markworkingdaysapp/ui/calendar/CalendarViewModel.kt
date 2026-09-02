package com.genius.markworkingdaysapp.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.common.buildMonthGrid
import com.genius.markworkingdaysapp.common.buildMonthItemsForYear
import com.genius.markworkingdaysapp.common.buildWeekdays
import com.genius.markworkingdaysapp.data.repository.SettingsRepository
import com.genius.markworkingdaysapp.data.repository.WorkDayRepository
import com.genius.markworkingdaysapp.model.AppSettings
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.model.MonthStatus
import com.genius.markworkingdaysapp.model.WorkDay
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialogUiState
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.toMonthItemUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class CalendarViewModel(
    private val settingsRepository: SettingsRepository,
    private val workDayRepository: WorkDayRepository,
) : ViewModel() {

    private val _displayedMonth = MutableStateFlow(YearMonth.now())
    private val _yearMonthDialogState = MutableStateFlow<YearMonthDialogUiState?>(null)
    private var yearMonthDialogLoadingJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> =
        _displayedMonth.flatMapLatest { displayedMonth ->
            combine(
                settingsRepository.settings,
                workDayRepository.observeWorkDaysInRange(
                    displayedMonth.atDay(1),
                    displayedMonth.atEndOfMonth(),
                ),
                workDayRepository.observeDailyRateForMonth(displayedMonth),
                _yearMonthDialogState,
            ) { settings, workDays, savedMonthRate, yearMonthDialogState ->
                createUiState(
                    displayedMonth = displayedMonth,
                    displayedMonthDailyRate = savedMonthRate ?: settings.dailyRate,
                    settings = settings,
                    workDays = workDays,
                    yearMonthDialogState = yearMonthDialogState,
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = createUiState(
                displayedMonth = _displayedMonth.value,
                displayedMonthDailyRate = settingsRepository.settings.value.dailyRate,
                settings = settingsRepository.settings.value,
                workDays = emptyMap(),
                yearMonthDialogState = _yearMonthDialogState.value
            )
        )

    private fun createUiState(
        displayedMonth: YearMonth,
        displayedMonthDailyRate: Int,
        settings: AppSettings,
        workDays: Map<LocalDate, WorkDay>,
        yearMonthDialogState: YearMonthDialogUiState?,
    ): CalendarUiState {

        val currentMonth = YearMonth.now()
        val monthStatistics = calculateMonthStatistics(workDays.values)

        val monthStatus = when {
            displayedMonth == currentMonth -> MonthStatus.CURRENT
            monthStatistics.workedDays > 0 -> MonthStatus.PAST_WORKED
            else -> MonthStatus.PAST_NOT_WORKED
        }

        return CalendarUiState(
            displayedMonthItem = displayedMonth.toMonthItemUiState(monthStatus),
            displayedMonthDailyRate = displayedMonthDailyRate,
            currencyLabel = settings.currencyLabel,
            monthGrid = buildMonthGrid(
                yearMonth = displayedMonth,
                firstDayOfWeek = settings.firstDayOfWeek,
                workDays = workDays,
            ),
            daysOfWeek = buildWeekdays(settings.firstDayOfWeek),
            monthStatistics = monthStatistics,
            yearMonthDialogState = yearMonthDialogState,
        )
    }

    fun onSaveDay(workDay: WorkDay) {
        viewModelScope.launch {
            workDayRepository.saveDay(
                workDay = workDay,
                defaultDailyRate = settingsRepository.settings.value.dailyRate,
            )
        }
    }

    fun onMonthSelected(month: YearMonth) {
        if (month <= YearMonth.now()) {
            _displayedMonth.value = month
        }
    }

    fun onDailyRateSaved(dailyRate: Int) {
        viewModelScope.launch {
            workDayRepository.setDailyRateForMonth(
                dailyRate = dailyRate,
                month = _displayedMonth.value,
            )
        }
    }

    fun onYearMonthDialogOpen() {
        loadDialogYear(Year.from(_displayedMonth.value))
    }

    fun onYearMonthDialogYearChanged(year: Year) {
        loadDialogYear(year)
    }

    fun onYearMonthDialogYearDismiss() {
        yearMonthDialogLoadingJob?.cancel()
        _yearMonthDialogState.value = null
    }

    private fun loadDialogYear(year: Year) {
        yearMonthDialogLoadingJob?.cancel()

        _yearMonthDialogState.value = YearMonthDialogUiState(
            displayedYear = year,
            monthItems = buildMonthItemsForYear(
                year = year,
                workDays = emptyMap(),
            ),
            isLoading = true,
        )

        yearMonthDialogLoadingJob = viewModelScope.launch {
            val workDays = workDayRepository.observeWorkDaysInRange(
                from = year.atDay(1),
                to = year.atDay(year.length())
            ).first()

            _yearMonthDialogState.update { currentState ->
                currentState?.copy(
                    monthItems = buildMonthItemsForYear(
                        year = year,
                        workDays = workDays,
                    ),
                    isLoading = false,
                )
            }
        }
    }

    private fun calculateMonthStatistics(
        workDays: Collection<WorkDay>,
    ): MonthStatistics {
        val workedDays = workDays.count { workDay ->
            workDay.status == DayStatus.FULL_DAY ||
                    workDay.status == DayStatus.SHORT_DAY
        }

        return MonthStatistics(
            workedDays = workedDays,
            totalBonuses = workDays.sumOf { workDay -> workDay.bonus ?: 0 },
            totalEarned = workDays.sumOf { workDay -> workDay.earned },

            )
    }

}