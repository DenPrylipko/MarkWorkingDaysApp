package com.genius.markworkingdaysapp.ui.xml.main

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.databinding.ViewSettingsDrawerBinding
import com.genius.markworkingdaysapp.common.formatTime
import com.genius.markworkingdaysapp.common.hapticClick
import com.genius.markworkingdaysapp.model.SettingsDrawerState
import java.time.DayOfWeek

class SettingsDrawerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSettingsDrawerBinding =
        ViewSettingsDrawerBinding.inflate(LayoutInflater.from(context), this, true)

    private var state: SettingsDrawerState? = null


    var onDailyRateChange: ((String) -> Unit)? = null
    var onCurrencyChange: ((String) -> Unit)? = null
    var onMondayClick: (() -> Unit)? = null
    var onSundayClick: (() -> Unit)? = null
    var onRemindToLogDayOnOffClick: (() -> Unit)? = null
    var onReminderTimeClick: (() -> Unit)? = null

    init {
        setup()
    }

    fun render(state: SettingsDrawerState) {
        this.state = state
        updateUi()
    }

    private fun setup() = with(binding) {

        etDailyRate.doAfterTextChanged { editable ->
            onDailyRateChange?.invoke(editable?.toString().orEmpty())
        }

        etCurrency.doAfterTextChanged { editable ->
            onCurrencyChange?.invoke(editable?.toString().orEmpty())
        }

        containerMonday.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            onMondayClick?.invoke()
        }

        containerSunday.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            onSundayClick?.invoke()
        }

        containerRemindToLogDayOnOff.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            onRemindToLogDayOnOffClick?.invoke()
        }

        containerReminderTime.setOnClickListener {
            it.hapticClick(HapticFeedbackConstants.CLOCK_TICK)
            onReminderTimeClick?.invoke()
        }


    }

    private fun updateUi() = with(binding) {
        val state = this@SettingsDrawerView.state ?: return@with

        val dailyRateText = if (state.dailyRate == 0) "" else state.dailyRate.toString()
        if (!etDailyRate.hasFocus() && etDailyRate.text?.toString() != dailyRateText) {
            etDailyRate.setText(dailyRateText)
        }

        if (!etCurrency.hasFocus() && etCurrency.text?.toString() != state.currency) {
            etCurrency.setText(state.currency)
        }

        if (state.firstDayOfWeek == DayOfWeek.SUNDAY) {
            tvChooseMonday.alpha = 0.5f
            tvChooseSunday.alpha = 1f
        } else {
            tvChooseMonday.alpha = 1f
            tvChooseSunday.alpha = 0.5f
        }

        tvReminderTime.text = formatTime(state.reminderHour, state.reminderMinute)

        if (state.notificationsEnabled) {
            layoutSetupRemindToLogDay.alpha = 1f
            tvRemindToLogDayOnOff.setTextColor(ContextCompat.getColor(context, R.color.text_bright))
            tvRemindToLogDayOnOff.backgroundTintList = ContextCompat.getColorStateList(context,R.color.full_day)
            tvRemindToLogDayOnOff.text = resources.getString(R.string.on)
        } else {
            layoutSetupRemindToLogDay.alpha = 0.7f
            tvRemindToLogDayOnOff.setTextColor(ContextCompat.getColor(context, R.color.text_secondary))
            tvRemindToLogDayOnOff.backgroundTintList = ContextCompat.getColorStateList(context,R.color.edit_text_bg)
            tvRemindToLogDayOnOff.text = resources.getString(R.string.off)
        }


    }
}