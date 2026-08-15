package com.genius.markworkingdaysapp.ui.xml.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.DialogDayCellEditBinding
import com.genius.markworkingdaysapp.common.hapticClick
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.DayData
import com.genius.markworkingdaysapp.model.DayType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDate

class DayDialogFragment(
    private val day: DayCell,
    private val onConfirm: (DayData) -> Unit
) : DialogFragment() {

    private var _binding: DialogDayCellEditBinding? = null
    private val binding get() = _binding!!
    private var currentDayType: DayType? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogDayCellEditBinding.inflate(LayoutInflater.from(requireContext()))

        setUIFromDayType(day.dayType)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.tvBtnConfirm.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.hapticClick(HapticFeedbackConstants.CONFIRM)
            } else {
                it.hapticClick(HapticFeedbackConstants.CONTEXT_CLICK)
            }
            onConfirm(getDataFromUI())
            dismiss()
        }
        binding.layoutFullDay.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            setUIFromDayType(DayType.FULL)
        }
        binding.layoutShortDay.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            setUIFromDayType(DayType.SHORT)
        }
        binding.layoutNotWorked.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            setUIFromDayType(DayType.NOT_WORKED)
        }

        binding.etAmount.doOnTextChanged { text, _, _, _ ->

            when (currentDayType) {
                DayType.FULL -> {
                    if (text.toString().toIntOrNull() == null && !text.toString().isBlank()) {
                        binding.tvBtnConfirm.alpha = 0.7f
                        binding.tvBtnConfirm.isEnabled = false
                    } else {
                        binding.tvBtnConfirm.alpha = 1f
                        binding.tvBtnConfirm.isEnabled = true
                    }
                }

                DayType.SHORT -> {
                    if (text.toString().toIntOrNull() == null) {
                        binding.tvBtnConfirm.alpha = 0.7f
                        binding.tvBtnConfirm.isEnabled = false
                    } else {
                        binding.tvBtnConfirm.alpha = 1f
                        binding.tvBtnConfirm.isEnabled = true
                    }
                }

                else -> return@doOnTextChanged

            }
        }

        return dialog
    }


    private fun getDataFromUI(): DayData {
        val date = day.date
        val dayType = when {
            binding.layoutFullDay.alpha == 1f -> DayType.FULL
            binding.layoutShortDay.alpha == 1f -> DayType.SHORT
            else -> DayType.NOT_WORKED
        }
        val bonus: Int? = if (dayType == DayType.FULL)
            binding.etAmount.text.toString().toIntOrNull()
        else null
        val shortDayEarned = if (dayType == DayType.SHORT)
            binding.etAmount.text.toString().toIntOrNull()
        else null
        val note = binding.etNote.text.toString()

        return DayData(date, dayType, bonus, shortDayEarned, note)

    }

    private fun setUIFromDayType(dayType: DayType?) {
        val date = day.date
        val bonus = day.bonus
        val shortDayEarned = day.earned
        val note = day.note
        binding.tvDate.text = date.dayOfMonth.toString()
        when (dayType) {
            DayType.FULL -> {
                if (date == LocalDate.now()) {
                    binding.tvDate.setTextColor(resources.getColor(R.color.accent_blue))
                } else {
                binding.tvDate.setTextColor(resources.getColor(R.color.full_day))
                    }
                binding.mainLayoutEditDay.setBackgroundResource(R.drawable.shape_stroke_full_day)

                binding.layoutShortDay.alpha = 0.7f
                binding.layoutFullDay.alpha = 1f
                binding.layoutNotWorked.alpha = 0.7f

                binding.tvBonusEarned.setTextColor(resources.getColor(R.color.full_day))
                binding.tvBonusEarned.setText(R.string.dialog_bonus)
                binding.tvBonusEarned.compoundDrawableTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.full_day)
                binding.tvOptional.isVisible = true

                if (bonus != null && bonus != 0) {
                    binding.etAmount.setText(bonus.toString())
                } else {
                    binding.etAmount.setText("")
                }

                binding.layoutBonusEarned.isVisible = true
                binding.layoutDayOff.isVisible = false

                binding.tvBtnConfirm.alpha = 1f
                binding.tvBtnConfirm.isEnabled = true

                currentDayType = DayType.FULL

            }

            DayType.SHORT -> {
                if (date == LocalDate.now()) {
                    binding.tvDate.setTextColor(resources.getColor(R.color.accent_blue))
                } else {
                    binding.tvDate.setTextColor(resources.getColor(R.color.short_day))
                }
                binding.mainLayoutEditDay.setBackgroundResource(R.drawable.shape_stroke_short_day)

                binding.layoutFullDay.alpha = 0.7f
                binding.layoutShortDay.alpha = 1f
                binding.layoutNotWorked.alpha = 0.7f

                binding.tvBonusEarned.setTextColor(resources.getColor(R.color.short_day))
                binding.tvBonusEarned.setText(R.string.dialog_earned)
                binding.tvBonusEarned.compoundDrawableTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.short_day)

                binding.tvOptional.isVisible = false

                if (day.dayType == DayType.SHORT) {
                    binding.etAmount.setText(shortDayEarned.toString())
                    binding.tvBtnConfirm.alpha = 1f
                    binding.tvBtnConfirm.isEnabled = true
                } else {
                    binding.etAmount.setText("")
                    binding.tvBtnConfirm.alpha = 0.7f
                    binding.tvBtnConfirm.isEnabled = false
                }


                binding.layoutBonusEarned.isVisible = true
                binding.layoutDayOff.isVisible = false

                currentDayType = DayType.SHORT

            }

            DayType.NOT_WORKED -> {
                if (date == LocalDate.now()) {
                    binding.tvDate.setTextColor(resources.getColor(R.color.accent_blue))
                } else {
                    binding.tvDate.setTextColor(resources.getColor(R.color.not_worked))
                }
                binding.mainLayoutEditDay.setBackgroundResource(R.drawable.shape_stroke_not_worked)

                binding.layoutFullDay.alpha = 0.7f
                binding.layoutShortDay.alpha = 0.7f
                binding.layoutNotWorked.alpha = 1f

                binding.layoutBonusEarned.isVisible = false
                binding.layoutDayOff.isVisible = true
                binding.tvOptional.isVisible = false

                binding.tvBtnConfirm.alpha = 1f
                binding.tvBtnConfirm.isEnabled = true

                currentDayType = DayType.NOT_WORKED
            }

            else -> {
                if (date == LocalDate.now()) {
                    binding.tvDate.setTextColor(resources.getColor(R.color.accent_blue))
                } else {
                    binding.tvDate.setTextColor(resources.getColor(R.color.not_worked))
                }
                binding.mainLayoutEditDay.setBackgroundResource(R.drawable.shape_stroke_not_worked)

                binding.layoutFullDay.alpha = 0.7f
                binding.layoutShortDay.alpha = 0.7f
                binding.layoutNotWorked.alpha = 1f

                binding.layoutBonusEarned.isVisible = false
                binding.layoutDayOff.isVisible = true
                binding.tvOptional.isVisible = true

                binding.tvBtnConfirm.alpha = 1f
                binding.tvBtnConfirm.isEnabled = true

                currentDayType = null
            }
        }
        binding.etNote.setText(if (!note.isNullOrBlank()) note else "")

        val drawable = binding.mainLayoutEditDay.background.mutate() as GradientDrawable

        drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.bg_edit_day))

        binding.mainLayoutEditDay.background = drawable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}