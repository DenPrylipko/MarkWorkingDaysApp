package com.genius.markworkingdaysapp.ui.main.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemMonthDayBinding
import com.genius.markworkingdaysapp.ui.main.model.DayCell
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

            val strokeColorDefault = ContextCompat.getColor(root.context, R.color.month_day_stroke_default)
            val textColorDefault = ContextCompat.getColor(root.context, R.color.month_day_text_default)
            val strokeWidthDefault = root.resources.getDimensionPixelSize(R.dimen.day_cell_stroke_width_default)
            val textSizeDefault = root.resources.getDimension(R.dimen.day_cell_text_size_default)

            val strokeColorToday = ContextCompat.getColor(root.context, R.color.month_day_stroke_today)
            val textColorToday = ContextCompat.getColor(root.context, R.color.month_day_text_today)
            val strokeWidthToday = root.resources.getDimensionPixelSize(R.dimen.day_cell_stroke_width_today)
            val textSizeToday = root.resources.getDimension(R.dimen.day_cell_text_size_today)

            val strokeColorWorked = ContextCompat.getColor(root.context, R.color.month_day_stroke_worked)
            val textColorWorked = ContextCompat.getColor(root.context, R.color.month_day_text_worked)


            tvDay.text = element.date.dayOfMonth.toString()

            root.alpha = if (element.isInCurrentMonth) 1f else 0.25f

            if (element.date == LocalDate.now()) {
                dayCard.strokeColor = if (element.worked) strokeColorWorked else strokeColorToday
                tvDay.setTextColor(textColorToday)
                dayCard.strokeWidth = strokeWidthToday
                tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeToday)
            } else {
                dayCard.strokeColor = if (element.worked) strokeColorWorked else strokeColorDefault
                tvDay.setTextColor(if (element.worked) textColorWorked else textColorDefault)
                dayCard.strokeWidth = strokeWidthDefault
                tvDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
            }

            ivNote.visibility = if (element.note == null) View.GONE else View.VISIBLE

            if (element.bonus != null && element.bonus > 0 && element.isInCurrentMonth) {
                tvBonus.visibility = View.VISIBLE
                tvBonus.text = "+${element.bonus}"
            } else {
                tvBonus.visibility = View.GONE
            }

            dayCard.setOnClickListener {
                if (!element.isInCurrentMonth) return@setOnClickListener

                if (element.date > LocalDate.now()) {
                    Toast.makeText(
                        root.context,
                        "cant edit this day.",
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