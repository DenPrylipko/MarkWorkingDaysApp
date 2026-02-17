package com.genius.markworkingdaysapp.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.DialogDayCellEditBinding
import com.genius.markworkingdaysapp.ui.main.model.DayCell
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate

class DayDialogFragment(
    private val currentDay: DayCell,
    private val workedThisDay: Boolean? = null,
    private val onSave: (date: LocalDate, worked: Boolean, bonus: Int?, note: String?) -> Unit
) : DialogFragment() {

    private var _binding: DialogDayCellEditBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDayCellEditBinding.inflate(LayoutInflater.from(requireContext()))

        val date = currentDay.date
        val worked = currentDay.worked
        val bonus = currentDay.bonus
        val note = currentDay.note

        with(binding) {
            tvDate.text = date.toString()
            if (workedThisDay ?: worked)
                rbWorked.isChecked = true
            else
                rbNotWorked.isChecked = true
            etBonus.setText(if (bonus == null || bonus <= 0) "" else bonus.toString())
            etNote.setText(if (note == null || note.isBlank()) "" else note)
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        binding.imgBtnConfirm.setOnClickListener {

            val worked = binding.rbWorked.isChecked
            val bonus = binding.etBonus.text.toString().toIntOrNull()
            val note = binding.etNote.text?.toString()?.takeIf { it.isNotBlank() }

            if (!worked && bonus != null) {
                Toast.makeText(context, "you can't have bonus if you didn't work", Toast.LENGTH_SHORT).show()
                binding.etBonus.setTextColor(ContextCompat.getColor(binding.root.context, R.color.error))
            } else {
                onSave(date, worked, bonus, note)
                dismiss()
            }

        }

        binding.imgBtnBack.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}