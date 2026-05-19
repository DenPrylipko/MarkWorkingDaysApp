package com.genius.markworkingdaysapp.ui.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.genius.markworkingdaysapp.databinding.DialogMonthChooseBinding
import com.genius.markworkingdaysapp.ui.common.GridSpacingItemDecoration
import com.genius.markworkingdaysapp.ui.common.NoScrollGridLayoutManager
import com.genius.markworkingdaysapp.ui.main.adapters.MonthsAdapter
import com.genius.markworkingdaysapp.ui.main.models.MainUiState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.YearMonth

class MonthChooseDialogFragment(
    private val yearMonth: YearMonth,
    private val onMonthChoose: (YearMonth) -> Unit
) : DialogFragment() {

    private var _binding: DialogMonthChooseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private var monthsAdapter: MonthsAdapter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogMonthChooseBinding.inflate(LayoutInflater.from(requireContext()) )

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
            viewModel.onPreviousYearChange()
        }

        binding.imgBtnNextYear.setOnClickListener {
            viewModel.onNextYearChange()
        }

        return dialog

    }

    private fun render(state: MainUiState) {

        monthsAdapter?.submitList(state.monthItems)
        binding.tvCurrentYear.text = state.currentYear.toString()
    }

    private fun setupRecyclerView() {
        binding.rvMonths.layoutManager = NoScrollGridLayoutManager(requireContext(), 2)
        monthsAdapter = MonthsAdapter() {
            onMonthChoose(it.yearMonth)
            dismiss()
        }
        binding.rvMonths.adapter = monthsAdapter
        binding.rvMonths.addItemDecoration(GridSpacingItemDecoration(2, 0, true))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setCurrentYear(viewModel.uiState.value.now.year)
        _binding = null
    }
}

