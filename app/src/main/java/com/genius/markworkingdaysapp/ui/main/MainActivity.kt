package com.genius.markworkingdaysapp.ui.main

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.data.NotificationHelper
import com.genius.markworkingdaysapp.data.ReminderScheduler
import com.genius.markworkingdaysapp.getMonthTitle
import com.genius.markworkingdaysapp.databinding.ActivityMainBinding
import com.genius.markworkingdaysapp.ui.main.adapters.WeekdaysAdapter
import com.genius.markworkingdaysapp.ui.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.ui.main.adapters.MonthGridAdapter
import com.genius.markworkingdaysapp.ui.main.adapters.MonthsAdapter
import com.genius.markworkingdaysapp.ui.main.models.DayCell
import com.genius.markworkingdaysapp.ui.main.models.DayType
import com.genius.markworkingdaysapp.ui.main.models.MainUiState
import com.genius.markworkingdaysapp.ui.main.models.MonthItem
import com.genius.markworkingdaysapp.ui.main.models.MonthStatus
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val DAYS_IN_WEEK = 7

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var backCallback: OnBackPressedCallback
    private lateinit var monthDaysAdapter: MonthGridAdapter
    private lateinit var weekdaysAdapter: WeekdaysAdapter
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var scheduler: ReminderScheduler
    private val viewModel: MainViewModel by viewModels()
    private var currentState: MainUiState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduler = ReminderScheduler(this)

        notificationHelper = NotificationHelper(this)
        notificationHelper.createChannel()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets()
        setupRecyclerViews()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }

        binding.tvCurrentMonth.setOnClickListener {
            openMonthChooseDialog(viewModel.uiState.value.currentMonth)
        }
        binding.imgBtnSettings.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        binding.imgBtnCopy.setOnClickListener {
            copyData(currentState)
        }
        binding.layoutDayCard.setOnClickListener {

            val today = LocalDate.now()
            val dayCell = viewModel.uiState.value.monthDaysData.find { it.date == today }
                ?: DayCell(date = today, isInCurrentMonth = true)

            openDayEditDialog(dayCell)
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                binding.touchBlocker.visibility = View.VISIBLE
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.touchBlocker.visibility = View.GONE
            }
        })

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                when {
                    binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    else -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this@MainActivity, backCallback)
    }

    override fun onResume() {
        super.onResume()

        NotificationManagerCompat.from(this)
            .cancel(NotificationHelper.NOTIFICATION_ID)
    }

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                notificationHelper.showTestNotification()
            }
        }

    private fun openMonthChooseDialog(yearMonth: YearMonth) {

        MonthChooseDialogFragment(yearMonth) {
            viewModel.onMonthChange(it)
        }.show(supportFragmentManager, "MonthChooseDialog")
    }

    private fun openDayEditDialog(day: DayCell) {

        DayDialogFragment(day) { data ->
            viewModel.onSaveDay(
                data.date,
                data.dayType,
                data.bonus,
                data.shortDayEarned,
                data.note
            )
        }.show(supportFragmentManager, "DayDialog")
    }

    private fun copyData(state: MainUiState?) = with(binding) {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        if (state == null) return@with
        val daysList = state.monthDaysData

        val text =
            daysList.filter { day -> day.isInCurrentMonth && day.dayType == DayType.FULL || day.dayType == DayType.SHORT }
                .joinToString("\n") { day ->
                    "${day.date.format(formatter)} " +
                            when (day.dayType) {
                                DayType.FULL if day.bonus != null && day.bonus != 0 -> {
                                    "+${day.bonus} ${state.settingsDrawerState.currency}"
                                }

                                DayType.SHORT -> {
                                    "${day.earned} ${state.settingsDrawerState.currency}"
                                }

                                else -> {
                                    ""
                                }
                            }
                }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("work_days", text)

        clipboard.setPrimaryClip(clip)
    }

    private fun render(state: MainUiState) = with(binding) {

        layoutDayCard.isVisible = state.currentMonth.monthValue == state.now.monthValue
        setCurrentMonthView(state)
        monthDaysAdapter.updateItems(state.monthDaysData)
        weekdaysAdapter.updateItems(state.weekdaysData)

        tvCurrentMonth.text = getMonthTitle(state.currentMonth)
        tvDailyRate.text = getString(
            R.string.text_daily_rate,
            state.settingsDrawerState.dailyRate,
            state.settingsDrawerState.currency
        )
        tvWorkingDays.text = resources.getQuantityString(
            R.plurals.working_days,
            state.monthStats.workingDays,
            state.monthStats.workingDays
        )
        tvTotalBonuses.text = getString(
            R.string.text_total_bonuses,
            state.monthStats.totalBonuses,
            state.settingsDrawerState.currency
        )
        tvTotalEarned.text = getString(
            R.string.text_total_earned,
            state.monthStats.totalEarned,
            state.settingsDrawerState.currency
        )

        currentState = state


        settingsDrawerView.render(state.settingsDrawerState)

        settingsDrawerView.onDailyRateChange = { string ->
            viewModel.onDailyRateChange(
                dailyRate = string.toIntOrNull() ?: 0
            )
        }

        settingsDrawerView.onCurrencyChange = { string ->
            viewModel.onCurrencyChange(string)
        }

        settingsDrawerView.onMondayClick = {
            viewModel.onMondayClicked()
        }
        settingsDrawerView.onSundayClick = {
            viewModel.onSundayClicked()
        }
        settingsDrawerView.onRemindToLogDayOnOffClick = {
            viewModel.onRemindToLogDayOnOffClicked()

            if (!state.settingsDrawerState.notificationsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        scheduler.schedule(
                            state.settingsDrawerState.reminderHour,
                            state.settingsDrawerState.reminderMinute
                        )
                    } else {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                } else {
                    scheduler.schedule(
                        state.settingsDrawerState.reminderHour,
                        state.settingsDrawerState.reminderMinute
                    )
                }
            } else {
                scheduler.cancel()
            }
        }
        settingsDrawerView.onReminderTimeClick = reminderTimeClick@ {
            if (!state.settingsDrawerState.notificationsEnabled) return@reminderTimeClick

            showTimePicker(
                hour = state.settingsDrawerState.reminderHour,
                minute = state.settingsDrawerState.reminderMinute
            )
        }

        val dayData = state.monthDaysData.find { it.date == state.now }

        layoutDayCard.setData(dayData, state.settingsDrawerState.currency, state.settingsDrawerState.dailyRate)

    }

    private fun setCurrentMonthView(state: MainUiState) = with(binding) {
        val item = state.monthItems.find { it.yearMonth == state.currentMonth }
            ?: MonthItem(
                state.currentMonth,
                getMonthTitle(state.currentMonth),
                MonthStatus.CURRENT
            )

        when(item.status) {
            MonthStatus.CURRENT -> {
                tvCurrentMonth.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_stroke_month_item_current)
            }
            MonthStatus.PAST_NOT_WORKED -> {
                tvCurrentMonth.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_stroke_month_item_past_not_worked)
            }

            MonthStatus.PAST_WORKED -> {
                tvCurrentMonth.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_stroke_month_item_past_worked)
            }

            MonthStatus.FUTURE -> {
                tvCurrentMonth.background = ContextCompat.getDrawable(this@MainActivity, R.drawable.shape_stroke_month_item_future)
            }
        }

    }

    private fun setupRecyclerViews() = with(binding) {
        rvMonthDays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)
        rvWeekdays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)

        monthDaysAdapter = MonthGridAdapter(emptyList()) { dayCell ->
            openDayEditDialog(dayCell)
        }
        weekdaysAdapter = WeekdaysAdapter(emptyList())

        rvMonthDays.adapter = monthDaysAdapter
        rvWeekdays.adapter = weekdaysAdapter

        rvMonthDays.addItemDecoration(
            GridSpacingItemDecoration(
                DAYS_IN_WEEK, 10, false
            )
        )
        rvWeekdays.addItemDecoration(
            GridSpacingItemDecoration(
                DAYS_IN_WEEK, 10, false
            )
        )
    }

    private fun showTimePicker(hour: Int, minute: Int) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute

            viewModel.onReminderTimeSelected(
                hour = hour,
                minute = minute
            )

            if (viewModel.uiState.value.settingsDrawerState.notificationsEnabled) {
                scheduler.cancel()
                scheduler.schedule(hour, minute)
            }
        }

        picker.show(supportFragmentManager, "reminder_time_picker")
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutMain) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, bars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutDrawerSettings) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, bars.top, v.paddingRight, bars.bottom)
            insets
        }
    }

}
