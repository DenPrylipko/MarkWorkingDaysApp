package com.genius.markworkingdaysapp.ui.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.buildMonthItemsForYear
import com.genius.markworkingdaysapp.getMonthTitle
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.databinding.ActivityMainBinding
import com.genius.markworkingdaysapp.ui.adapter.WeekdaysAdapter
import com.genius.markworkingdaysapp.ui.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.ui.main.adapters.MonthGridAdapter
import com.genius.markworkingdaysapp.ui.main.adapters.MonthsAdapter
import com.genius.markworkingdaysapp.ui.main.models.DayType
import com.genius.markworkingdaysapp.ui.main.models.MainUiState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

private const val DAYS_IN_WEEK = 7

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var backCallback: OnBackPressedCallback
    private val viewModel: MainViewModel by viewModels()
    private lateinit var monthDaysAdapter: MonthGridAdapter
    private lateinit var weekdaysAdapter: WeekdaysAdapter
    private lateinit var monthsAdapter: MonthsAdapter
    private var currentState: MainUiState? = null
    var uiSettingsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        binding.etDailyRate.doAfterTextChanged { editable ->
            val value = editable?.toString()?.toIntOrNull() ?: 0
            viewModel.setDailyRate(value)
        }
        binding.etCurrency.doAfterTextChanged { editable ->
            val value = editable?.toString() ?: ""
            viewModel.setCurrency(value)
        }
        binding.rgChooseFirstDayOfWeek.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbMonday -> viewModel.setFirstDayOfWeek(AppSettings.FirstDayOfWeek.MONDAY)
                R.id.rbSunday -> viewModel.setFirstDayOfWeek(AppSettings.FirstDayOfWeek.SUNDAY)
            }
        }


        binding.tvCurrentDate.setOnClickListener {
            binding.layoutMonthChoose.isVisible = true
            binding.touchBlocker.isVisible = true
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

                    binding.layoutMonthChoose.isVisible -> {
                        binding.layoutMonthChoose.isGone = true
                        binding.touchBlocker.isGone = true
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
                                    "+ ${day.bonus} ${state.currency}"
                                }

                                DayType.SHORT -> {
                                    "${day.earned} ${state.currency}"
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

        currentState = state

        if (!uiSettingsInitialized) {
            etDailyRate.setText(state.dailyRate.toString())
            etCurrency.setText(state.currency)

            uiSettingsInitialized = true
        }

        tvCurrentDate.text = getMonthTitle(state.currentMonth)
        tvDailyRate.text = getString(
            R.string.text_daily_rate,
            state.dailyRate,
            state.currency
        )
        tvWorkingDays.text = resources.getQuantityString(
            R.plurals.working_days,
            state.monthStats.workingDays,
            state.monthStats.workingDays
        )
        tvTotalBonuses.text = getString(
            R.string.text_total_bonuses,
            state.monthStats.totalBonuses,
            state.currency
        )
        tvTotalEarned.text = getString(
            R.string.text_total_earned,
            state.monthStats.totalEarned,
            state.currency
        )

        if (state.firstDayOfWeek == DayOfWeek.SUNDAY)
            rbSunday.isChecked = true
        else
            rbMonday.isChecked = true

        monthDaysAdapter.updateItems(state.monthDaysData)
        weekdaysAdapter.updateItems(state.weekdaysData)
    }

    private fun setupRecyclerViews() = with(binding) {
        rvMonthDays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)
        rvWeekdays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)
        rvMonths.layoutManager = GridLayoutManager(this@MainActivity, 1)

        monthDaysAdapter = MonthGridAdapter(emptyList()) { dayCell ->
            val currentDay =
                viewModel.uiState.value.monthDaysData.find { it.date == dayCell.date } ?: dayCell

            DayDialogFragment(currentDay) { data ->
                viewModel.onSaveDay(
                    data.date,
                    data.dayType,
                    data.bonus,
                    data.shortDayEarned,
                    data.note
                )
            }.show(supportFragmentManager, "DayDialog")
        }
        weekdaysAdapter = WeekdaysAdapter(emptyList())
        monthsAdapter = MonthsAdapter(buildMonthItemsForYear(2026)) { month ->
            viewModel.setCurrentMonth(month.yearMonth)
            layoutMonthChoose.isGone = true
            touchBlocker.isGone = true
        }

        rvMonthDays.adapter = monthDaysAdapter
        rvWeekdays.adapter = weekdaysAdapter
        rvMonths.adapter = monthsAdapter

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
        rvMonths.addItemDecoration(
            GridSpacingItemDecoration(
                1, 10, false
            )
        )
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
