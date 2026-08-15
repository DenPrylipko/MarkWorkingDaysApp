package com.genius.markworkingdaysapp.ui.xml.main

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ViewTodayStatusBinding
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.DayType
import java.time.LocalDate

class TodayStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewTodayStatusBinding

    init{
        binding = ViewTodayStatusBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    fun setData(dayData: DayCell?, currency: String, dailyRate: Int) {
        render(dayData, currency, dailyRate)
    }

    @SuppressLint("SetTextI18n")
    private fun render(dayData: DayCell?, currency: String, dailyRate: Int) {

        binding.tvDate.text = LocalDate.now().dayOfMonth.toString()

        if (dayData == null) {
            binding.viewMainLayout.background =
                AppCompatResources.getDrawable(context,R.drawable.shape_stroke_not_worked)

            binding.tvDayStatus.text = context.getString(R.string.view_no_info)
            binding.dayType.isVisible = false

            binding.layoutDayInfo.isVisible = false
            binding.layoutTapToSet.isVisible = true

            return
        }

        when (dayData.dayType) {
            DayType.FULL -> {
                val earned = dailyRate + (dayData.bonus ?: 0)

                binding.viewMainLayout.background =
                    AppCompatResources.getDrawable(context,R.drawable.shape_stroke_full_day)

                binding.tvDayStatus.text = context.getString(R.string.view_worked)

                binding.tvEarned.isVisible = true

                binding.layoutDayOff.isVisible = false
                binding.dayType.isVisible = true

                binding.tvDayTypeFull.isVisible = true
                binding.tvDayTypeShort.isVisible = false
                binding.tvDayTypeNotWorked.isVisible = false

                binding.layoutDayInfo.isVisible = true
                binding.layoutTapToSet.isVisible = false

                if (dayData.bonus != 0 && dayData.bonus != null) {
                    binding.tvBonus.text = context.getString(R.string.view_bonus, dayData.bonus, currency)
                    binding.tvBonus.isVisible = true
                } else {
                    binding.tvBonus.isGone = true
                }

                if (dayData.note.isNullOrBlank()) {
                    binding.tvNoteView.text = context.getString(R.string.view_no_note)
                } else {
                    binding.tvNoteView.text = "\"${dayData.note}\""
                }

                binding.tvEarned.text = context.getString(R.string.view_earned, earned, currency)

            }
            DayType.SHORT -> {

                binding.viewMainLayout.background =
                    AppCompatResources.getDrawable(context,R.drawable.shape_stroke_short_day)

                binding.tvDayStatus.text = context.getString(R.string.view_worked)

                binding.dayType.isVisible = true
                binding.tvEarned.isVisible = true

                binding.layoutDayOff.isVisible = false

                binding.tvDayTypeFull.isVisible = false
                binding.tvDayTypeShort.isVisible = true
                binding.tvDayTypeNotWorked.isVisible = false

                binding.layoutDayInfo.isVisible = true
                binding.layoutTapToSet.isVisible = false
                binding.tvBonus.isGone = true

                if (dayData.note.isNullOrBlank()) {
                    binding.tvNoteView.text = context.getString(R.string.view_no_note)
                } else {
                    binding.tvNoteView.text = "\"${dayData.note}\""
                }

                binding.tvEarned.text = context.getString(R.string.view_earned, dayData.earned, currency)


            }
            DayType.NOT_WORKED -> {
                binding.viewMainLayout.background =
                    AppCompatResources.getDrawable(context,R.drawable.shape_stroke_not_worked)

                binding.tvDayStatus.text = ""
                binding.layoutDayOff.isVisible = true
                binding.tvEarned.isInvisible = true

                binding.dayType.isVisible = true

                binding.tvDayTypeFull.isVisible = false
                binding.tvDayTypeShort.isVisible = false
                binding.tvDayTypeNotWorked.isVisible = true

                binding.layoutDayInfo.isVisible = true
                binding.layoutTapToSet.isVisible = false
                binding.tvBonus.isGone = true

                if (dayData.note.isNullOrBlank()) {
                    binding.tvNoteView.text = context.getString(R.string.view_no_note)
                } else {
                    binding.tvNoteView.text = "\"${dayData.note}\""
                }

                binding.tvEarned.text = context.getString(R.string.dialog_day_off)
            }
            else -> {
                binding.viewMainLayout.background =
                    AppCompatResources.getDrawable(context,R.drawable.shape_stroke_not_worked)

                binding.tvDayStatus.text = context.getString(R.string.view_no_info)
                binding.dayType.isVisible = false

                binding.layoutDayInfo.isVisible = false
                binding.layoutTapToSet.isVisible = true
            }

        }


    }

}