package com.genius.markworkingdaysapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.genius.markworkingdaysapp.common.buildMonthGridBase
import com.genius.markworkingdaysapp.common.buildMonthItemsForYear
import com.genius.markworkingdaysapp.common.buildWeekdays
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.db.WorkDayEntity
import com.genius.markworkingdaysapp.data.db.WorkDayRepository
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.DayType
import com.genius.markworkingdaysapp.model.MainConfig
import com.genius.markworkingdaysapp.model.MainUiState
import com.genius.markworkingdaysapp.model.ChooseMonthDialogState
import com.genius.markworkingdaysapp.model.MonthGridBase
import com.genius.markworkingdaysapp.model.MonthStats
import com.genius.markworkingdaysapp.model.RVData
import com.genius.markworkingdaysapp.model.SettingsDrawerState
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

    private val dateFlow = MutableStateFlow(LocalDate.now())
    private val pickerDateFlow = MutableStateFlow(LocalDate.now())
    private val todayCheckedFlow = MutableStateFlow(settings.todayChecked)

    // region: Settings
    private val currencyFlow = MutableStateFlow(settings.currency)
    private val firstDowFlow = MutableStateFlow(getFirstDOW())
    private val dailyRateFlow = MutableStateFlow(settings.dailyRate)
    private val notificationsEnabledFlow = MutableStateFlow(settings.notificationsEnabled)
    private val reminderHourFlow = MutableStateFlow(settings.reminderHour)
    private val reminderMinuteFlow = MutableStateFlow(settings.reminderMinute)
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

    private val repo: WorkDayRepository by lazy {
        val db = DatabaseProvider.get(app)
        WorkDayRepository(db.workDayDao())
    }

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

    private fun getFirstDOW(): DayOfWeek = when (settings.firstDayOfWeek) {
        AppSettings.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
        AppSettings.FirstDayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
    }

    private fun buildMonthDays(dbMap: Map<Long, WorkDayEntity>, monthGridBase: MonthGridBase, config: MainConfig): List<DayCell> {
        val monthDaysData: List<DayCell> = monthGridBase.cells.map { baseCell ->
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
        return monthDaysData
    }

    private val monthChooseDialogFlow =
        combine(
            pickerDateFlow,
            workedMonthsFlow,
        ) { pickerDate, workedMonths ->

            val monthItems = buildMonthItemsForYear(
                year = Year.from(pickerDate),
                workedMonths
            )

            ChooseMonthDialogState(
                year = Year.from(pickerDate),
                monthItems = monthItems,
            )
        }

    // region: Statistics

    private fun calculateStats(monthDaysData: List<DayCell>, config: MainConfig): MonthStats {
        val currentMonthDays = monthDaysData.filter { it.isInCurrentMonth }

        val workingDays =
            currentMonthDays.count { it.dayType == DayType.FULL || it.dayType == DayType.SHORT }
        val totalBonuses = currentMonthDays.sumOf { it.bonus ?: 0 }

        val fullDaysEarned =
            currentMonthDays.count { it.dayType == DayType.FULL } * config.dailyRate

        val shortDayEarned = currentMonthDays.sumOf {
            if (it.dayType == DayType.SHORT) it.earned ?: 0 else 0
        }

        val totalEarned = fullDaysEarned + shortDayEarned + totalBonuses

        return MonthStats(workingDays, totalBonuses, totalEarned)

    }

    private val mainConfigFlow =
        combine(
            dateFlow,
            firstDowFlow,
            dailyRateFlow,
            currencyFlow,
        ) { date, firstDOW, dailyRate, currency ->
            MainConfig(
                date = date,
                firstDOW = firstDOW,
                currentMonth = YearMonth.from(date),
                dailyRate = dailyRate,
                currency = currency,
            )
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

                val weekdaysData = buildWeekdays(config.firstDOW)
                val monthGridBase = buildMonthGridBase(
                    yearMonth = YearMonth.from(config.date),
                    firstDayOfWeek = config.firstDOW
                )

                repo.observeRangeMap(monthGridBase.start, monthGridBase.end)
                    .map { dbMap ->
                       val monthDaysData = buildMonthDays(dbMap, monthGridBase, config)

                        setTodayChecked(
                            monthDaysData.find { it.date == config.date }?.hasEntry ?: false
                        )

                        MainUiState(
                            date = config.date,
                            monthStats = calculateStats(monthDaysData, config),
                            rvData = RVData(
                                monthDaysData,
                                weekdaysData,
                                monthChooseState.monthItems
                            ),
                            settingsDrawerState = settingsDrawerState,
                            chooseMonthDialogState = ChooseMonthDialogState(
                                Year.from(config.date),
                                monthChooseState.monthItems
                            )
                        )
                    }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                initialMainUiState()
            )

    fun initialMainUiState() = MainUiState(
        date = LocalDate.now(),
        monthStats = MonthStats(0, 0, 0),
        rvData = RVData(
            emptyList(),
            buildWeekdays(firstDowFlow.value),
            emptyList()
        ),
        settingsDrawerState = SettingsDrawerState(
            dailyRate = dailyRateFlow.value,
            currency = currencyFlow.value,
            firstDayOfWeek = firstDowFlow.value,
            notificationsEnabled = notificationsEnabledFlow.value,
            reminderHour = reminderHourFlow.value,
            reminderMinute = reminderMinuteFlow.value,
            todayChecked = todayCheckedFlow.value
        ),
        chooseMonthDialogState = ChooseMonthDialogState(Year.from(LocalDate.now()), emptyList())
    )

    // region: Actions

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
    fun onYearChange(year: Int) {
        setPickerYear(year)
    }

    fun onMonthChange(yearMonth: YearMonth) {
        dateFlow.value = yearMonth.atDay(1)
        setPickerYear(yearMonth.year)
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

    fun setNotificationsEnabled(value: Boolean) {
        settings.notificationsEnabled = value
        notificationsEnabledFlow.value = value
    }

    fun setReminderTime(hour: Int, minute: Int) {
        settings.setReminderTime(hour, minute)
        reminderHourFlow.value = hour
        reminderMinuteFlow.value = minute
    }

    fun setPickerYear(value: Int) {
        pickerDateFlow.value = pickerDateFlow.value.withYear(value)
    }

    fun setTodayChecked(value: Boolean) {
        settings.todayChecked = value
        todayCheckedFlow.value = value
    }
}