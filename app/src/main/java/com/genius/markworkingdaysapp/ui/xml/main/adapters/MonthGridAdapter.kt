package com.genius.markworkingdaysapp.ui.xml.main.adapters

import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemMonthDayBinding
import com.genius.markworkingdaysapp.model.DayCell
import com.genius.markworkingdaysapp.model.DayType
import java.time.LocalDate

class MonthGridAdapter(
    private var items: List<DayCell>,
    private val onClick: (DayCell) -> Unit
) : RecyclerView.Adapter<MonthGridAdapter.MonthDayViewHolder>() {

    class MonthDayViewHolder(val binding: ItemMonthDayBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthDayViewHolder {
        val binding = ItemMonthDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MonthDayViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MonthDayViewHolder,
        position: Int
    ) {
        val element = items[position]

        with(holder.binding) {

            val strokeColorNotWorked =
                ContextCompat.getColor(root.context, R.color.calendar_default)
            val textColorNotWorked =
                ContextCompat.getColor(root.context, R.color.calendar_default)
            val strokeWidthDefault =
                root.resources.getDimensionPixelSize(R.dimen.day_cell_stroke_width_default)
            val textSizeDefault = root.resources.getDimension(R.dimen.day_cell_text_size_default)

            val strokeColorToday =
                ContextCompat.getColor(root.context, R.color.calendar_today)
            val textColorToday = ContextCompat.getColor(root.context, R.color.calendar_today)
            val strokeWidthToday =
                root.resources.getDimensionPixelSize(R.dimen.day_cell_stroke_width_today)
            val textSizeToday = root.resources.getDimension(R.dimen.day_cell_text_size_today)

            val strokeColorFullDay =
                ContextCompat.getColor(root.context, R.color.full_day)
            val textColorFullDay =
                ContextCompat.getColor(root.context, R.color.full_day)

            val strokeColorShortDay =
                ContextCompat.getColor(root.context, R.color.short_day)
            val textColorShortDay =
                ContextCompat.getColor(root.context, R.color.short_day)


            tvDay.text = element.date.dayOfMonth.toString()

            root.alpha = if (element.isInCurrentMonth) 1f else 0.25f

            ivNote.isVisible = !element.note.isNullOrBlank()
            when (element.dayType) {
                DayType.FULL -> {
                    dayCard.strokeColor = strokeColorFullDay
                    tvDay.setTextColor(textColorFullDay)
                    dayCard.strokeWidth = strokeWidthDefault
                    tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
                    tvBonus.text =
                        if (element.bonus != null && element.bonus != 0) "+${element.bonus}" else ""
                    tvBonus.isVisible = true
                }

                DayType.SHORT -> {
                    dayCard.strokeColor = strokeColorShortDay
                    tvDay.setTextColor(textColorShortDay)
                    dayCard.strokeWidth = strokeWidthDefault
                    tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
                    tvBonus.text = element.earned.toString()
                    tvBonus.isVisible = true
                }

                else -> {
                    dayCard.strokeColor = strokeColorNotWorked
                    tvDay.setTextColor(textColorNotWorked)
                    dayCard.strokeWidth = strokeWidthDefault
                    tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
                    tvBonus.isGone = true
                }


            }
            if (element.date.year == LocalDate.now().year
                && element.date.monthValue == LocalDate.now().monthValue
                && element.date.dayOfMonth == LocalDate.now().dayOfMonth
            ) {
                dayCard.strokeColor = strokeColorToday
                tvDay.setTextColor(textColorToday)
                dayCard.strokeWidth = strokeWidthToday
                tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeToday)

            }

            if (!element.isInCurrentMonth) {
                dayCard.strokeColor = strokeColorNotWorked
                tvDay.setTextColor(textColorNotWorked)
                dayCard.strokeWidth = strokeWidthDefault
                tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
                tvBonus.isGone = true
                ivNote.isGone = true
            }



            dayCard.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                if (!element.isInCurrentMonth) return@setOnClickListener

                if (element.date > LocalDate.now()) {
                    Toast.makeText(
                        root.context,
                        R.string.toast_cant_edit_day,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                onClick(element)
            }
        }

    }

    override fun getItemCount(): Int = items.size


    fun updateItems(newItems: List<DayCell>) {
        items = newItems
        notifyDataSetChanged()
    }

}