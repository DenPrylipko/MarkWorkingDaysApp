package com.genius.markworkingdaysapp.ui.main.adapters

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemMonthBinding
import com.genius.markworkingdaysapp.ui.main.models.MonthItem
import com.genius.markworkingdaysapp.ui.main.models.MonthStatus

class MonthsAdapter(
    private val onClick: (MonthItem) -> Unit
) : ListAdapter<MonthItem, MonthsAdapter.MonthsViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<MonthItem>() {
        override fun areItemsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean =
            oldItem.yearMonth == newItem.yearMonth

            override fun areContentsTheSame(oldItem: MonthItem, newItem: MonthItem): Boolean =
                oldItem == newItem
    }

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
        val element = getItem(position)
        setMonthCardFor(holder, element)
    }

    private fun setMonthCardFor(holder: MonthsViewHolder, element: MonthItem) = with(holder.binding) {

        with(holder.binding) {
            tvMonthTitle.text = element.title

            when(element.status) {
                MonthStatus.CURRENT -> {
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_current)
                    monthCard.alpha = 1f
                }
                MonthStatus.PAST_NOT_WORKED -> {
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_past_not_worked)
                    monthCard.alpha = 1f
                }

                MonthStatus.PAST_WORKED -> {
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_past_worked)
                    monthCard.alpha = 1f
                }

                MonthStatus.FUTURE -> {
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_future)
                    monthCard.alpha = 0.7f
                }
            }

            monthCard.setOnClickListener {
                if (element.status != MonthStatus.FUTURE) {
                    onClick(element)
                }
            }
        }
    }
}