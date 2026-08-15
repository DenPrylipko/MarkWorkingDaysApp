package com.genius.markworkingdaysapp.ui.xml.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.genius.markworkingdaysapp.MainViewModel
import com.genius.markworkingdaysapp.databinding.DialogMonthChooseBinding
import com.genius.markworkingdaysapp.ui.xml.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.xml.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.common.hapticClick
import com.genius.markworkingdaysapp.ui.xml.main.adapters.MonthsAdapter
import com.genius.markworkingdaysapp.model.MainUiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.Year
import java.time.YearMonth

class MonthChooseDialogFragment(
    private val enteredYear: Year,
    private val onMonthChoose: (YearMonth) -> Unit
) : DialogFragment() {

    private var _binding: DialogMonthChooseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private var monthsAdapter: MonthsAdapter? = null
    private var year = enteredYear
    private var lastRenderedYear: Year? = null
    private var firstRenderDone = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMonthChooseBinding.inflate(LayoutInflater.from(requireContext()))

        setupRecyclerView()
        render(viewModel.uiState.value)

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                render(uiState)
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.imgBtnPreviousYear.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            animateButtonPress(it)
            year = year.minusYears(1)
            viewModel.onYearChange(year.value)
        }

        binding.imgBtnNextYear.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            animateButtonPress(it)
            year = year.plusYears(1)
            viewModel.onYearChange(year.value)
        }

        return dialog

    }

    private fun render(state: MainUiState) {
        val newYear = year

        if (!firstRenderDone) {
            applyState(state)
            firstRenderDone = true
            lastRenderedYear = newYear
            return
        }

        if (lastRenderedYear != newYear) {
            animateContentChange {
                applyState(state)
            }
        } else {
            applyState(state)
        }

        lastRenderedYear = newYear

    }

    private fun setupRecyclerView() {
        binding.rvMonths.layoutManager = NoScrollGridLayoutManager(requireContext(), 2)

        binding.rvMonths.itemAnimator = null

        monthsAdapter = MonthsAdapter {
            onMonthChoose(it.yearMonth)
            dismiss()
        }
        binding.rvMonths.adapter = monthsAdapter
        binding.rvMonths.addItemDecoration(GridSpacingItemDecoration(2, 0, true))
    }

    private fun applyState(state: MainUiState) {

        monthsAdapter?.submitList(state.chooseMonthDialogState.monthItems)
        binding.tvCurrentYear.text = year.toString()
    }

    private fun animateContentChange(action: () -> Unit) {

        binding.chooseMonthContentContainer.animate()
            .alpha(0.1f)
            .scaleX(0.98f)
            .scaleY(0.98f)
            .translationX(-50f)
            .rotation(0.8f)
            .setDuration(120)
            .setInterpolator(OvershootInterpolator(2f))
            .withEndAction {

                action()
                binding.chooseMonthContentContainer.translationX = 50f

                binding.chooseMonthContentContainer.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationX(0f)
                    .rotation(0f)
                    .setDuration(60)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()
            }
            .start()
    }

    private fun animateButtonPress(view: View) {
        view.animate()
            .scaleY(0.88f)
            .scaleX(0.88f)
            .setDuration(80)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(OvershootInterpolator(2f))
                    .start()

            }
            .start()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setPickerYear(viewModel.uiState.value.date.year)
        _binding = null
    }
}

