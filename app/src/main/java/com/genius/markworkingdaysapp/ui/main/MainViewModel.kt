package com.genius.markworkingdaysapp.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.buildMonthGridBase
import com.genius.markworkingdaysapp.buildMonthItemsForYear
import com.genius.markworkingdaysapp.buildWeekdays
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.db.WorkDayRepository
import com.genius.markworkingdaysapp.ui.main.models.DateChooseDialogState
import com.genius.markworkingdaysapp.ui.main.models.DayCell
import com.genius.markworkingdaysapp.ui.main.models.DayType
import com.genius.markworkingdaysapp.ui.main.models.MainConfig
import com.genius.markworkingdaysapp.ui.main.models.MainUiState
import com.genius.markworkingdaysapp.ui.main.models.MonthItem
import com.genius.markworkingdaysapp.ui.main.models.MonthStats
import com.genius.markworkingdaysapp.ui.main.models.SettingsDrawerState
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
import java.time.Year
import java.time.YearMonth


class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val settings = AppSettings(app.applicationContext)

    private val repo: WorkDayRepository by lazy {
        val db = DatabaseProvider.get(app)
        WorkDayRepository(db.workDayDao())
    }

    private val shownDateFlow = MutableStateFlow(LocalDate.now())
    private val currentYearFlow = MutableStateFlow(Year.now())
    private val currentMonthFlow = MutableStateFlow(YearMonth.now())

    private val firstDowFlow = MutableStateFlow(getFirstDOW())
    private val currencyFlow = MutableStateFlow(settings.currency)
    private val dailyRateFlow = MutableStateFlow(settings.dailyRate)
    private val notificationsEnabledFlow = MutableStateFlow(settings.notificationsEnabled)
    private val reminderHourFlow = MutableStateFlow(settings.reminderHour)
    private val reminderMinuteFlow = MutableStateFlow(settings.reminderMinute)

    private val todayCheckedFlow = MutableStateFlow(settings.todayChecked)
    private val workedMonthsFlow: StateFlow<Set<YearMonth>> =
        repo.observeWorkedEpochDays()
            .map { epochDays ->
                epochDays
                    .map { epochDays ->
                        val date = LocalDate.ofEpochDay(epochDays)
                        YearMonth.from(date)
                    }
                    .toSet()
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet()
            )

    private val monthItemsFlow: StateFlow<List<MonthItem>> =
        combine(
            currentYearFlow,
            workedMonthsFlow
        ) { year, workedMonth ->
            buildMonthItemsForYear(year.value, workedMonth)
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    private val reminderSettingsFlow =
        combine(
            notificationsEnabledFlow,
            reminderHourFlow,
            reminderMinuteFlow
        ) { enabled, hour, minute ->
            Triple(enabled, hour, minute)
        }

    private val settingsDrawerFlow =
        combine(
            dailyRateFlow,
            currencyFlow,
            firstDowFlow,
            reminderSettingsFlow,
            todayCheckedFlow
        ) { dailyRate, currency, firstDayOfWeek, reminderSettings, todayChecked ->

            val (notificationsEnabled, reminderHour, reminderMinute) = reminderSettings

            SettingsDrawerState(
                dailyRate = dailyRate,
                currency = currency,
                firstDayOfWeek = firstDayOfWeek,
                notificationsEnabled = notificationsEnabled,
                reminderHour = reminderHour,
                reminderMinute = reminderMinute,
                todayChecked = todayChecked
            )
        }

    private val monthChooseDialogFlow =
        combine(
            currentYearFlow,
            monthItemsFlow,
        ) { currentYear, monthItems ->
            DateChooseDialogState(
                currentYear,
                monthItems,
            )
        }

    private val mainConfigFlow =
        combine(
            shownDateFlow,
            firstDowFlow,
            currentMonthFlow,
            dailyRateFlow,
            currencyFlow,
        ) { shownDate, firstDOW, currentMonth, dailyRate, currency ->
            MainConfig(
                shownDate = shownDate,
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
        combine(
            mainConfigFlow,
            settingsDrawerFlow,
            monthChooseDialogFlow
        ) { config, settingsDrawerState, monthChooseState ->
            Triple(config, settingsDrawerState, monthChooseState)
        }
            .flatMapLatest { (config, settingsDrawerState, monthChooseState) ->

                val weekdays = buildWeekdays(config.firstDOW)
                val monthGridBase = buildMonthGridBase(
                    year = config.currentMonth.year,
                    month = config.currentMonth.month.value,
                    firstDayOfWeek = config.firstDOW
                )

                repo.observeRangeMap(monthGridBase.start, monthGridBase.end)
                    .map { dbMap ->
                        val daysList: List<DayCell> = monthGridBase.cells.map { baseCell ->
                            val e = dbMap[baseCell.date.toEpochDay()]
                            baseCell.copy(
                                hasEntry = e != null,
                                isInCurrentMonth = baseCell.date.month.value == config.currentMonth.month.value,
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

                        setTodayChecked(
                            daysList.find { it.date == config.shownDate }?.hasEntry ?: false
                        )

                        val currentMonthDays = daysList.filter { it.isInCurrentMonth }

                        val workingDays =
                            currentMonthDays.count { it.dayType == DayType.FULL || it.dayType == DayType.SHORT }
                        val totalBonuses = currentMonthDays.sumOf { it.bonus ?: 0 }

                        val fullDaysEarned =
                            currentMonthDays.count { it.dayType == DayType.FULL } * config.dailyRate

                        val shortDayEarned = currentMonthDays.sumOf {
                            if (it.dayType == DayType.SHORT) it.earned ?: 0 else 0
                        }

                        val totalEarned = fullDaysEarned + shortDayEarned + totalBonuses

                        MainUiState(
                            now = config.shownDate,
                            currentMonth = config.currentMonth,
                            currentYear = monthChooseState.currentYear,
                            monthStats = MonthStats(workingDays, totalBonuses, totalEarned),
                            monthDaysData = daysList,
                            weekdaysData = weekdays,
                            monthItems = monthChooseState.monthItems,
                            settingsDrawerState = settingsDrawerState
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
        currentMonth = currentMonthFlow.value,
        currentYear = currentYearFlow.value,
        monthStats = MonthStats(0, 0, 0),
        monthDaysData = emptyList(),
        weekdaysData = buildWeekdays(firstDowFlow.value),
        monthItems = monthItemsFlow.value,
        settingsDrawerState = SettingsDrawerState(
            dailyRate = dailyRateFlow.value,
            currency = currencyFlow.value,
            firstDayOfWeek = firstDowFlow.value,
            notificationsEnabled = notificationsEnabledFlow.value,
            reminderHour = reminderHourFlow.value,
            reminderMinute = reminderMinuteFlow.value,
            todayChecked = todayCheckedFlow.value
        )
    )

    fun onSaveDay(
        date: LocalDate,
        dayType: DayType,
        bonus: Int?,
        shortDayEarned: Int?,
        note: String?
    ) {
        viewModelScope.launch {
            repo.saveDay(date, dayType, bonus, shortDayEarned, note)
        }
    }

    fun onPreviousYearChange() {
        currentYearFlow.value = currentYearFlow.value.minusYears(1)
    }

    fun onNextYearChange() {
        currentYearFlow.value = currentYearFlow.value.plusYears(1)
    }

    fun onMonthChange(yearMonth: YearMonth) {
        shownDateFlow.value = yearMonth.atDay(1)
        setCurrentMonth(yearMonth)
        setCurrentYear(yearMonth.year)
    }

    fun onDailyRateChange(dailyRate: Int) {
        setDailyRate(dailyRate)
    }

    fun onCurrencyChange(currency: String) {
        setCurrency(currency)
    }

    fun onMondayClicked() {
        setFirstDayOfWeek(AppSettings.FirstDayOfWeek.MONDAY)
    }

    fun onSundayClicked() {
        setFirstDayOfWeek(AppSettings.FirstDayOfWeek.SUNDAY)
    }

    fun onRemindToLogDayOnOffClicked() {
        setNotificationsEnabled(!settings.notificationsEnabled)
    }

    fun onReminderTimeSelected(hour: Int, minute: Int) {
        setReminderTime(hour, minute)
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

    fun setNotificationsEnabled(value: Boolean) {
        settings.notificationsEnabled = value
        notificationsEnabledFlow.value = value
    }

    fun setReminderTime(hour: Int, minute: Int) {
        settings.setReminderTime(hour, minute)
        reminderHourFlow.value = hour
        reminderMinuteFlow.value = minute
    }

    fun setCurrentYear(value: Int) {
        currentYearFlow.value = Year.of(value)
    }

    fun setTodayChecked(value: Boolean) {
        settings.todayChecked = value
        todayCheckedFlow.value = value
    }
}
