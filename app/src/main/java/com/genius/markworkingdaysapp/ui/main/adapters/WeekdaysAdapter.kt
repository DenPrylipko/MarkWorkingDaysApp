package com.genius.markworkingdaysapp.ui.main.adapters

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemWeekdayBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class WeekdaysAdapter(
    private var items: List<DayOfWeek>
) : RecyclerView.Adapter<WeekdaysAdapter.WeekdayViewHolder>() {

    class WeekdayViewHolder(val binding: ItemWeekdayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeekdayViewHolder {
        val binding = ItemWeekdayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeekdayViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WeekdayViewHolder,
        position: Int
    ) {
        val element = items[position]

        with(holder.binding) {

            val textSizeToday = root.resources.getDimension(R.dimen.weekday_text_size_today)
            val textSizeDefault = root.resources.getDimension(R.dimen.weekday_text_size_default)

            tvWeekday.text = element.getDisplayName(TextStyle.SHORT, Locale.getDefault())

            if (element == LocalDate.now().dayOfWeek) {
                tvWeekday.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeToday)
            } else {
                tvWeekday.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeDefault)
            }

        }

    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<DayOfWeek>) {
        items = newItems
        notifyDataSetChanged()
    }


}