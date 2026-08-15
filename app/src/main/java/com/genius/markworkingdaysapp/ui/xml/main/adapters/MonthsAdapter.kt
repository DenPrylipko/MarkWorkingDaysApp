package com.genius.markworkingdaysapp.ui.xml.main.adapters

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ItemMonthBinding
import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.common.hapticClick
import com.genius.markworkingdaysapp.model.MonthItem
import com.genius.markworkingdaysapp.model.MonthStatus

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
            tvMonthTitle.text = getMonthTitle(element.yearMonth)

            when(element.status) {
                MonthStatus.CURRENT -> {
                    tvMonthTitle.setTextColor(ContextCompat.getColor(root.context, R.color.accent_blue))
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_current)
                    monthCard.alpha = 1f
                }
                MonthStatus.PAST_NOT_WORKED -> {
                    tvMonthTitle.setTextColor(ContextCompat.getColor(root.context, R.color.not_worked))
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_past_not_worked)
                    monthCard.alpha = 1f
                }

                MonthStatus.PAST_WORKED -> {
                    tvMonthTitle.setTextColor(ContextCompat.getColor(root.context, R.color.full_day))
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_past_worked)
                    monthCard.alpha = 1f
                }

                MonthStatus.FUTURE -> {
                    tvMonthTitle.setTextColor(ContextCompat.getColor(root.context, R.color.accent_gray))
                    tvMonthTitle.background = ContextCompat.getDrawable(root.context, R.drawable.shape_stroke_month_item_future)
                    monthCard.alpha = 0.5f
                }
            }

            monthCard.setOnClickListener {
                it.hapticClick(HapticFeedbackConstants.CONTEXT_CLICK)
                if (element.status != MonthStatus.FUTURE) {
                    onClick(element)
                }
            }
        }
    }
}