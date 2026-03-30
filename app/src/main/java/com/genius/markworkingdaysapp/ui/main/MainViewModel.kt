package com.genius.markworkingdaysapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.db.WorkDayRepository
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.buildMonthGridBase
import com.genius.markworkingdaysapp.buildWeekdays
import com.genius.markworkingdaysapp.ui.main.models.DayCell
import com.genius.markworkingdaysapp.ui.main.models.DayType
import com.genius.markworkingdaysapp.ui.main.models.MainConfig
import com.genius.markworkingdaysapp.ui.main.models.MainUiState
import com.genius.markworkingdaysapp.ui.main.models.MonthStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = AppSettings(app.applicationContext)

    private val repo: WorkDayRepository by lazy {
        val db = DatabaseProvider.get(app)
        WorkDayRepository(db.workDayDao())
    }

    private val shownDate = MutableStateFlow(LocalDate.now())
    private val firstDowFlow = MutableStateFlow(getFirstDOW())
    private val dailyRateFlow = MutableStateFlow(settings.dailyRate)
    private val currencyFlow = MutableStateFlow(settings.currency)
    private val currentMonthFlow = MutableStateFlow(YearMonth.now())

    private val mainConfigFlow =
        combine(
            shownDate,
            firstDowFlow,
            currentMonthFlow,
            dailyRateFlow,
            currencyFlow,
        ) { now, firstDOW, currentMonth, dailyRate, currency ->
            MainConfig(
                now = now,
                firstDOW = firstDOW,
                currentMonth = currentMonth,
                dailyRate = dailyRate,
                currency = currency,
                )
        }

    private fun getFirstDOW(): DayOfWeek = when (settings.firstDayOfWeek) {
        AppSettings.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
        AppSettings.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
    }

    val uiState: StateFlow<MainUiState> =
        mainConfigFlow
            .flatMapLatest { config ->
                val weekdays = buildWeekdays(config.firstDOW)
                val monthGridBase = buildMonthGridBase(
                    year = config.now.year,
                    month = config.currentMonth.month.value,
                    firstDayOfWeek = config.firstDOW
                )

                repo.observeRange(monthGridBase.start, monthGridBase.end)
                    .map { dbMap ->
                        val daysList: List<DayCell> = monthGridBase.cells.map { baseCell ->
                            val e = dbMap[baseCell.date.toEpochDay()]
                            baseCell.copy(
                                hasEntry = e != null,
                                isInCurrentMonth = baseCell.date.monthValue == config.currentMonth.monthValue,
                                dayType = when {
                                    e == null -> null
                                    e.shortDayEarned != null -> DayType.SHORT
                                    e.worked -> DayType.FULL
                                    else -> DayType.NOT_WORKED
                                },
                                bonus = e?.bonus,
                                earned = when {
                                    e == null -> null
                                    e.shortDayEarned != null -> e.shortDayEarned
                                    e.worked -> config.dailyRate + (e.bonus ?: 0)
                                    else -> 0
                                },
                                note = e?.note,
                                )
                        }

                        val todayChecked = daysList.find { it.date == config.now }?.hasEntry ?: false

                        val currentMonthDays = daysList.filter { it.isInCurrentMonth }

                        val workingDays = currentMonthDays.count { it.dayType == DayType.FULL || it.dayType == DayType.SHORT }
                        val totalBonuses = currentMonthDays.sumOf { it.bonus ?: 0 }

                        val fullDaysEarned = currentMonthDays.count { it.dayType == DayType.FULL } * config.dailyRate

                        val shortDayEarned = currentMonthDays.sumOf {
                            if (it.dayType == DayType.SHORT) it.earned ?: 0 else 0
                        }

                        val totalEarned = fullDaysEarned + shortDayEarned + totalBonuses

                        MainUiState(
                            now = config.now,
                            firstDayOfWeek = config.firstDOW,
                            monthDaysData = daysList,
                            weekdaysData = weekdays,
                            dailyRate = config.dailyRate,
                            currency = config.currency,
                            todayCheckedStatus = todayChecked,
                            monthStats = MonthStats(workingDays, totalBonuses, totalEarned),
                            currentMonth = config.currentMonth
                        )
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialMainUiState()
            )

    fun initialMainUiState() = MainUiState(
        now = LocalDate.now(),
        firstDayOfWeek = firstDowFlow.value,
        monthDaysData = emptyList(),
        weekdaysData = buildWeekdays(firstDowFlow.value),
        dailyRate = dailyRateFlow.value,
        currency = currencyFlow.value,
        todayCheckedStatus = false,
        monthStats = MonthStats(0, 0, 0),
        currentMonth = currentMonthFlow.value
    )


    fun onSaveDay(date: LocalDate, dayType: DayType, bonus: Int?, shortDayEarned: Int?, note: String?) {
        viewModelScope.launch {
            repo.saveDay(date, dayType, bonus, shortDayEarned, note)
        }
    }

    fun setFirstDayOfWeek(value: AppSettings.FirstDayOfWeek) {
        settings.firstDayOfWeek = value
        firstDowFlow.value = getFirstDOW()
    }

    fun setDailyRate(value: Int) {
        settings.dailyRate = value
        dailyRateFlow.value = value
    }

    fun setCurrency(value: String) {
        settings.currency = value
        currencyFlow.value = value
    }

    fun setCurrentMonth(value: YearMonth) {
        currentMonthFlow.value = value
    }
}
