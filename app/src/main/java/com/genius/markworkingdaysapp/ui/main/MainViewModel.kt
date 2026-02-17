package com.genius.markworkingdaysapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.repository.WorkDayRepository
import com.genius.markworkingdaysapp.data.settings.AppSettings
import com.genius.markworkingdaysapp.core.data.buildMonthGridBase
import com.genius.markworkingdaysapp.core.data.buildWeekdays
import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity
import com.genius.markworkingdaysapp.ui.main.model.DayCell
import com.genius.markworkingdaysapp.ui.main.model.MainUiState
import com.genius.markworkingdaysapp.ui.main.model.MonthGridBase
import com.genius.markworkingdaysapp.ui.main.model.MonthStats
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month


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

    private fun getFirstDOW(): DayOfWeek = when (settings.firstDayOfWeek) {
        AppSettings.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
        AppSettings.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
    }

    val uiState: StateFlow<MainUiState> =
        combine(
            shownDate, firstDowFlow, dailyRateFlow, currencyFlow,
        ) { now, firstDOW, dailyRate, currency ->
            Triple(now, firstDOW, dailyRate)
        }
            .flatMapLatest { (now, firstDOW, dailyRate) ->
                val base: MonthGridBase = buildMonthGridBase(now.year, now.monthValue, firstDOW)
                val weekdays = buildWeekdays(firstDOW)
                val currency = currencyFlow.value

                repo.observeRange(base.start, base.end)
                    .map { dbMap ->
                        val monthDays: List<DayCell> = base.cells.map { baseCell ->
                            val e = dbMap[baseCell.date.toEpochDay()]
                            baseCell.copy(
                                worked = e?.worked ?: false,
                                bonus = e?.bonus ?: 0,
                                note = e?.note,
                                hasEntry = e != null
                            )
                        }


                        val todayChecked = monthDays.find { it.date == now }?.hasEntry ?: false

                        val workingDays = monthDays.count { it.worked }
                        val totalBonuses = monthDays.sumOf { it.bonus ?: 0 }
                        val totalEarned = workingDays * dailyRate + totalBonuses

                        MainUiState(
                            now = now,
                            firstDayOfWeek = firstDOW,
                            monthDaysData = monthDays,
                            weekdaysData = weekdays,
                            dailyRate = dailyRate,
                            currency = currency,
                            todayCheckedStatus = todayChecked,
                            monthStats = MonthStats(workingDays, totalBonuses, totalEarned)
                        )
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MainUiState(
                    now = LocalDate.now(),
                    firstDayOfWeek = firstDowFlow.value,
                    monthDaysData = emptyList(),
                    weekdaysData = buildWeekdays(firstDowFlow.value),
                    dailyRate = dailyRateFlow.value,
                    currency = currencyFlow.value,
                    todayCheckedStatus = false,
                    monthStats = MonthStats(0, 0, 0)
                )
            )

    fun onSaveDay(date: LocalDate, worked: Boolean, bonus: Int?, note: String?) {
        viewModelScope.launch {
            repo.saveDay(date, worked, bonus, note)
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

    fun refreshMonth() {
        shownDate.value = LocalDate.now()
    }
}
