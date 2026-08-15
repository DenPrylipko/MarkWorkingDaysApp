package com.genius.markworkingdaysapp.ui.xml

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import com.genius.markworkingdaysapp.MainViewModel
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.common.hapticClick
import com.genius.markworkingdaysapp.data.NotificationHelper
import com.genius.markworkingdaysapp.data.ReminderScheduler
import com.genius.markworkingdaysapp.databinding.ActivityMainBinding
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.DayType
import com.genius.markworkingdaysapp.model.MainUiState
import com.genius.markworkingdaysapp.model.MonthStatus
import com.genius.markworkingdaysapp.ui.xml.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.xml.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.ui.xml.main.DayDialogFragment
import com.genius.markworkingdaysapp.ui.xml.main.MonthChooseDialogFragment
import com.genius.markworkingdaysapp.ui.xml.main.adapters.MonthGridAdapter
import com.genius.markworkingdaysapp.ui.xml.main.adapters.WeekdaysAdapter
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private const val DAYS_IN_WEEK = 7

/**
 * Temporary Compose host for the old XML screen.
 *
 * The screen can now be replaced with Compose piece by piece while MainActivity
 * remains independent of the old ViewBinding implementation.
 */
@SuppressLint("ContextCastToActivity")
@Composable
fun MainXmlScreen(viewModel: MainViewModel = composeViewModel()) {
    val activity = LocalContext.current as AppCompatActivity
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val latestState = rememberUpdatedState(state)

    val binding = remember(activity) {
        ActivityMainBinding.inflate(activity.layoutInflater)
    }
    val notificationHelper = remember(activity) {
        NotificationHelper(activity)
    }
    val scheduler = remember(activity) {
        ReminderScheduler(activity)
    }

    val monthDaysAdapter = remember(activity, viewModel) {
        MonthGridAdapter(emptyList()) { dayCell ->
            openDayEditDialog(activity, viewModel, dayCell)
        }
    }
    val weekdaysAdapter = remember {
        WeekdaysAdapter(true, emptyList())
    }

    val requestNotificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            notificationHelper.showTestNotification()
        }
    }

    LaunchedEffect(notificationHelper) {
        notificationHelper.createChannel()
    }

    DisposableEffect(activity, binding, monthDaysAdapter, weekdaysAdapter) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        applyInsets(binding)
        setupRecyclerViews(
            activity = activity,
            binding = binding,
            monthDaysAdapter = monthDaysAdapter,
            weekdaysAdapter = weekdaysAdapter
        )

        binding.tvCurrentMonth.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            openMonthChooseDialog(
                activity = activity,
                viewModel = viewModel,
                date = latestState.value.date
            )
        }

        binding.imgBtnSettings.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.imgBtnCopy.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            copyData(activity, latestState.value)
        }

        binding.layoutDayCard.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)

            val today = LocalDate.now()
            val dayCell = latestState.value.rvData.monthDaysData
                .find { item -> item.date == today }
                ?: DayCell(date = today, isInCurrentMonth = true)

            openDayEditDialog(activity, viewModel, dayCell)
        }

        binding.settingsDrawerView.onDailyRateChange = { value ->
            viewModel.onDailyRateChange(value.toIntOrNull() ?: 0)
        }
        binding.settingsDrawerView.onCurrencyChange = viewModel::onCurrencyChange
        binding.settingsDrawerView.onMondayClick = viewModel::onMondayClicked
        binding.settingsDrawerView.onSundayClick = viewModel::onSundayClicked

        binding.settingsDrawerView.onRemindToLogDayOnOffClick = {
            val settings = latestState.value.settingsDrawerState
            viewModel.onRemindToLogDayOnOffClicked()

            if (!settings.notificationsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val granted = ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        scheduler.schedule(settings.reminderHour, settings.reminderMinute)
                    } else {
                        requestNotificationPermission.launch(
                            Manifest.permission.POST_NOTIFICATIONS
                        )
                    }
                } else {
                    scheduler.schedule(settings.reminderHour, settings.reminderMinute)
                }
            } else {
                scheduler.cancel()
            }
        }

        binding.settingsDrawerView.onReminderTimeClick = reminderTimeClick@{
            val settings = latestState.value.settingsDrawerState
            if (!settings.notificationsEnabled) return@reminderTimeClick

            showTimePicker(
                activity = activity,
                viewModel = viewModel,
                scheduler = scheduler,
                hour = settings.reminderHour,
                minute = settings.reminderMinute
            )
        }

        val drawerListener = object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                binding.touchBlocker.visibility = View.VISIBLE
            }

            override fun onDrawerClosed(drawerView: View) {
                hideKeyboard(binding)
                binding.touchBlocker.visibility = View.GONE
            }
        }
        binding.drawerLayout.addDrawerListener(drawerListener)

        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val isKeyboardVisible = ViewCompat.getRootWindowInsets(binding.root)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true

                when {
                    isKeyboardVisible -> hideKeyboard(binding)
                    binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }

                    else -> {
                        isEnabled = false
                        activity.onBackPressedDispatcher.onBackPressed()
                        isEnabled = true
                    }
                }
            }
        }
        activity.onBackPressedDispatcher.addCallback(activity, backCallback)

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                NotificationManagerCompat.from(activity)
                    .cancel(NotificationHelper.NOTIFICATION_ID)
            }
        }
        activity.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            activity.lifecycle.removeObserver(lifecycleObserver)
            backCallback.remove()
            binding.drawerLayout.removeDrawerListener(drawerListener)
            binding.tvCurrentMonth.setOnClickListener(null)
            binding.imgBtnSettings.setOnClickListener(null)
            binding.imgBtnCopy.setOnClickListener(null)
            binding.layoutDayCard.setOnClickListener(null)
            binding.rvMonthDays.adapter = null
            binding.rvWeekdays.adapter = null
        }
    }

    AndroidView(
        factory = { binding.root },
        modifier = Modifier.fillMaxSize(),
        update = {
            render(
                activity = activity,
                binding = binding,
                state = state,
                monthDaysAdapter = monthDaysAdapter,
                weekdaysAdapter = weekdaysAdapter
            )
        }
    )
}

private fun openMonthChooseDialog(
    activity: AppCompatActivity,
    viewModel: MainViewModel,
    date: LocalDate
) {
    MonthChooseDialogFragment(Year.from(date)) { selectedDate ->
        viewModel.onMonthChange(selectedDate)
    }.show(activity.supportFragmentManager, "MonthChooseDialog")
}

private fun openDayEditDialog(
    activity: AppCompatActivity,
    viewModel: MainViewModel,
    day: DayCell
) {
    DayDialogFragment(day) { data ->
        viewModel.onSaveDay(
            data.date,
            data.dayType,
            data.bonus,
            data.shortDayEarned,
            data.note
        )
    }.show(activity.supportFragmentManager, "DayDialog")
}

private fun hideKeyboard(binding: ActivityMainBinding) {
    ViewCompat.getWindowInsetsController(binding.root)
        ?.hide(WindowInsetsCompat.Type.ime())
}

private fun copyData(activity: AppCompatActivity, state: MainUiState) {
    val formatter = DateTimeFormatter.ofPattern("dd.MM")
    val filteredDays = state.rvData.monthDaysData.filter { day ->
        day.isInCurrentMonth &&
            (day.dayType == DayType.FULL || day.dayType == DayType.SHORT)
    }

    if (filteredDays.isEmpty()) {
        Toast.makeText(
            activity,
            R.string.toast_no_working_days,
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val text = filteredDays.joinToString("\n") { day ->
        "${day.date.format(formatter)} " +
            when (day.dayType) {
                DayType.FULL -> {
                    if (day.bonus != null && day.bonus != 0) {
                        "+${day.bonus} ${state.settingsDrawerState.currency} " +
                            (day.note.takeUnless { it.isNullOrBlank() } ?: "")
                    } else {
                        day.note.takeUnless { it.isNullOrBlank() } ?: ""
                    }
                }

                DayType.SHORT -> {
                    "${day.earned} ${state.settingsDrawerState.currency} " +
                        (day.note.takeUnless { it.isNullOrBlank() } ?: "")
                }

                else -> ""
            }
    }

    val clipboard = activity.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE)
        as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("work_days", text))
}

private fun render(
    activity: AppCompatActivity,
    binding: ActivityMainBinding,
    state: MainUiState,
    monthDaysAdapter: MonthGridAdapter,
    weekdaysAdapter: WeekdaysAdapter
) = with(binding) {
    val now = LocalDate.now()
    val isCurrentMonth = state.date.monthValue == now.monthValue &&
        state.date.year == now.year

    layoutDayCard.isVisible = isCurrentMonth
    setMonthViewItem(activity, binding, state)
    monthDaysAdapter.updateItems(state.rvData.monthDaysData)
    weekdaysAdapter.updateItems(state.rvData.weekdaysData, isCurrentMonth)

    tvCurrentMonth.text = getMonthTitle(YearMonth.from(state.date))
    tvDailyRate.text = activity.getString(
        R.string.text_daily_rate,
        state.settingsDrawerState.dailyRate,
        state.settingsDrawerState.currency
    )
    tvWorkingDays.text = activity.resources.getQuantityString(
        R.plurals.working_days,
        state.monthStats.workingDays,
        state.monthStats.workingDays
    )
    tvTotalBonuses.text = activity.getString(
        R.string.text_total_bonuses,
        state.monthStats.totalBonuses,
        state.settingsDrawerState.currency
    )
    tvTotalEarned.text = activity.getString(
        R.string.text_total_earned,
        state.monthStats.totalEarned,
        state.settingsDrawerState.currency
    )

    settingsDrawerView.render(state.settingsDrawerState)

    val dayData = state.rvData.monthDaysData.find { it.date == now }
    layoutDayCard.setData(
        dayData,
        state.settingsDrawerState.currency,
        state.settingsDrawerState.dailyRate
    )
}

private fun setMonthViewItem(
    activity: AppCompatActivity,
    binding: ActivityMainBinding,
    state: MainUiState
) = with(binding) {
    val yearMonth = YearMonth.from(state.date)
    val item = state.rvData.monthItemsData
        .find { it.yearMonth == yearMonth }
        ?: return@with

    when (item.status) {
        MonthStatus.CURRENT -> {
            tvCurrentMonth.setTextColor(
                ContextCompat.getColor(activity, R.color.accent_blue)
            )
            tvCurrentMonth.background = ContextCompat.getDrawable(
                activity,
                R.drawable.shape_stroke_month_item_current
            )
        }

        MonthStatus.PAST_NOT_WORKED -> {
            tvCurrentMonth.setTextColor(
                ContextCompat.getColor(activity, R.color.not_worked)
            )
            tvCurrentMonth.background = ContextCompat.getDrawable(
                activity,
                R.drawable.shape_stroke_month_item_past_not_worked
            )
        }

        MonthStatus.PAST_WORKED -> {
            tvCurrentMonth.setTextColor(
                ContextCompat.getColor(activity, R.color.full_day)
            )
            tvCurrentMonth.background = ContextCompat.getDrawable(
                activity,
                R.drawable.shape_stroke_month_item_past_worked
            )
        }

        MonthStatus.FUTURE -> {
            tvCurrentMonth.setTextColor(
                ContextCompat.getColor(activity, R.color.accent_gray)
            )
            tvCurrentMonth.background = ContextCompat.getDrawable(
                activity,
                R.drawable.shape_stroke_month_item_future
            )
        }
    }
}

private fun setupRecyclerViews(
    activity: AppCompatActivity,
    binding: ActivityMainBinding,
    monthDaysAdapter: MonthGridAdapter,
    weekdaysAdapter: WeekdaysAdapter
) = with(binding) {
    rvMonthDays.layoutManager = NoScrollGridLayoutManager(activity, DAYS_IN_WEEK)
    rvWeekdays.layoutManager = NoScrollGridLayoutManager(activity, DAYS_IN_WEEK)
    rvMonthDays.adapter = monthDaysAdapter
    rvWeekdays.adapter = weekdaysAdapter

    rvMonthDays.addItemDecoration(
        GridSpacingItemDecoration(DAYS_IN_WEEK, 10, false)
    )
    rvWeekdays.addItemDecoration(
        GridSpacingItemDecoration(DAYS_IN_WEEK, 10, false)
    )
}

private fun showTimePicker(
    activity: AppCompatActivity,
    viewModel: MainViewModel,
    scheduler: ReminderScheduler,
    hour: Int,
    minute: Int
) {
    val picker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_12H)
        .setHour(hour)
        .setMinute(minute)
        .setTitleText("Select time")
        .build()

    picker.addOnPositiveButtonClickListener {
        val selectedHour = picker.hour
        val selectedMinute = picker.minute

        viewModel.onReminderTimeSelected(
            hour = selectedHour,
            minute = selectedMinute
        )

        if (viewModel.uiState.value.settingsDrawerState.notificationsEnabled) {
            scheduler.cancel()
            scheduler.schedule(selectedHour, selectedMinute)
        }
    }

    picker.show(activity.supportFragmentManager, "reminder_time_picker")
}

private fun applyInsets(binding: ActivityMainBinding) {
    ViewCompat.setOnApplyWindowInsetsListener(binding.layoutMain) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(view.paddingLeft, bars.top, view.paddingRight, bars.bottom)
        insets
    }

    ViewCompat.setOnApplyWindowInsetsListener(binding.layoutDrawerSettings) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(view.paddingLeft, bars.top, view.paddingRight, bars.bottom)
        insets
    }

    ViewCompat.requestApplyInsets(binding.root)
}
