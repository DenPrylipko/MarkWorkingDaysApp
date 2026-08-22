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
import com.genius.markworkingdaysapp.model.WorkDay
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialogUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

class CalendarViewModel(
    private val settingsRepository: SettingsRepository,
    private val workDayRepository: WorkDayRepository,
) : ViewModel() {

    private val _displayedMonth = MutableStateFlow(YearMonth.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CalendarUiState> =
        _displayedMonth
            .flatMapLatest { displayedMonth ->
                val displayedYear = Year.from(displayedMonth)

                combine(
                    settingsRepository.settings,
                    workDayRepository.observeWorkDaysInRange(
                        displayedYear.atDay(1),
                        displayedYear.atDay(displayedYear.length()),
                    ),
                    workDayRepository.observeDailyRateForMonth(displayedMonth)
                ) { settings, workDays, savedMonthRate ->
                    createUiState(
                        displayedMonth = displayedMonth,
                        displayedMonthDailyRate = savedMonthRate ?: settings.dailyRate,
                        settings = settings,
                        workDays = workDays,
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
                )
            )

    private fun createUiState(
        displayedMonth: YearMonth,
        displayedMonthDailyRate: Int,
        settings: AppSettings,
        workDays: Map<LocalDate, WorkDay>,
    ): CalendarUiState {
        val displayedMonthWorkDays = workDays.filterKeys { date ->
            YearMonth.from(date) == displayedMonth
        }

        return CalendarUiState(
            displayedMonth = displayedMonth,
            displayedMonthDailyRate = displayedMonthDailyRate,
            monthGrid = buildMonthGrid(
                yearMonth = displayedMonth,
                firstDayOfWeek = settings.firstDayOfWeek,
                workDays = displayedMonthWorkDays,
            ),
            daysOfWeek = buildWeekdays(settings.firstDayOfWeek),
            monthStatistics = calculateMonthStatistics(displayedMonthWorkDays.values),
            yearMonthDialogState = YearMonthDialogUiState(
                year = Year.from(displayedMonth),
                monthItems = buildMonthItemsForYear(
                    year = Year.from(displayedMonth),
                    workDays = workDays,
                ),
            ),

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