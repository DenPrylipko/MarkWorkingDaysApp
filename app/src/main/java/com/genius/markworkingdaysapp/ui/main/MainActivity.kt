package com.genius.markworkingdaysapp.ui.main

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
import androidx.lifecycle.lifecycleScope
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.core.data.buildMonthGridBase
import com.genius.markworkingdaysapp.core.data.getMonthTitle
import com.genius.markworkingdaysapp.data.settings.AppSettings
import com.genius.markworkingdaysapp.databinding.ActivityMainBinding
import com.genius.markworkingdaysapp.ui.main.adapter.MonthGridAdapter
import com.genius.markworkingdaysapp.ui.adapter.WeekdaysAdapter
import com.genius.markworkingdaysapp.ui.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.ui.main.model.MainUiState
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.collections.emptyList

private const val DAYS_IN_WEEK = 7

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private lateinit var monthAdapter: MonthGridAdapter
    private lateinit var weekdaysAdapter: WeekdaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyInsets()

        setupRecyclerViews()

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                render(state)
            }
        }

        binding.tvCurrentDate.text = getMonthTitle(viewModel.uiState.value.now)

        binding.etDailyRate.setText(viewModel.uiState.value.dailyRate.toString())

        binding.etCurrency.setText(viewModel.uiState.value.currency)

        binding.etDailyRate.doAfterTextChanged { editable ->
            val value = editable?.toString()?.toIntOrNull() ?: 0
            viewModel.setDailyRate(value)
        }

        binding.rgChooseFirstDayOfWeek.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbMonday -> viewModel.setFirstDayOfWeek(AppSettings.FirstDayOfWeek.MONDAY)
                R.id.rbSunday -> viewModel.setFirstDayOfWeek(AppSettings.FirstDayOfWeek.SUNDAY)
            }
        }

        binding.etCurrency.doAfterTextChanged { editable ->
            val value = editable?.toString() ?: ""
            viewModel.setCurrency(value)
        }

        binding.drawerLayout.addDrawerListener(object : androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                binding.touchBlocker.visibility = View.VISIBLE
            }
            override fun onDrawerClosed(drawerView: View) {
                binding.touchBlocker.visibility = View.GONE
            }
        })

        binding.imgBtnSettings.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        binding.btnYes.setOnClickListener {
            DayDialogFragment(
                    viewModel.uiState.value.monthDaysData.find { it.date == LocalDate.now()}!!,
                true
                ) { date, worked, bonus, note ->
                    viewModel.onSaveDay(date, worked, bonus, note)
                }.show(supportFragmentManager, "DayDialog")
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

    }

    private fun setupRecyclerViews() = with(binding) {
        rvMonthDays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)
        rvWeekdays.layoutManager = NoScrollGridLayoutManager(this@MainActivity, DAYS_IN_WEEK)

        monthAdapter = MonthGridAdapter(emptyList()) { dayCell ->
            val currentDay = viewModel.uiState.value.monthDaysData.find { it.date == dayCell.date } ?: dayCell
            DayDialogFragment(
                currentDay
            ) { date, worked, bonus, note ->
                viewModel.onSaveDay(date, worked, bonus, note)
            }.show(supportFragmentManager, "DayDialog")
        }
        weekdaysAdapter = WeekdaysAdapter(emptyList())

        rvMonthDays.adapter = monthAdapter
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

    private fun render(state: MainUiState) = with(binding) {
        tvDailyRate.text = getString(R.string.text_daily_rate,state.dailyRate, state.currency)

        tvWorkingDays.text =
            resources.getQuantityString(
                R.plurals.working_days,
                state.monthStats.workingDays,
                state.monthStats.workingDays
            )
        tvTotalBonuses.text = getString(R.string.text_total_bonuses, state.monthStats.totalBonuses)
        tvTotalEarned.text = getString(R.string.text_total_earned, state.monthStats.totalEarned)

        layoutIfWorked.isGone = state.todayCheckedStatus

        if (state.firstDayOfWeek == DayOfWeek.SUNDAY) {
            rbSunday.isChecked = true
        } else {
            rbMonday.isChecked = true
        }

        monthAdapter.updateItems(state.monthDaysData)
        weekdaysAdapter.updateItems(state.weekdaysData)
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