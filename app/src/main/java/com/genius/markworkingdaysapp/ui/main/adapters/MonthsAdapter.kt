package com.genius.markworkingdaysapp.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemMonthBinding
import com.genius.markworkingdaysapp.ui.main.models.MonthItem
import com.genius.markworkingdaysapp.ui.main.models.MonthStatus

class MonthsAdapter(
    private var items: List<MonthItem>,
    private val onClick: (MonthItem) -> Unit
) : RecyclerView.Adapter<MonthsAdapter.MonthsViewHolder>() {

    class MonthsViewHolder(val binding: ItemMonthBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthsViewHolder {
        val binding = ItemMonthBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MonthsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthsViewHolder, position: Int) {
        val element = items[position]

        with(holder.binding) {
            tvMonthTitle.text = element.title

            monthCard.backgroundTintList = when (element.status) {
                MonthStatus.PAST -> ContextCompat.getColorStateList(
                    root.context,
                    R.color.secondary
                )

                MonthStatus.CURRENT -> ContextCompat.getColorStateList(
                    root.context,
                    R.color.primary
                )

                MonthStatus.FUTURE -> ContextCompat.getColorStateList(
                    root.context,
                    R.color.month_day_stroke_default
                )
            }

            monthCard.setOnClickListener {
                if (element.status == MonthStatus.FUTURE) return@setOnClickListener
                onClick(element)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}