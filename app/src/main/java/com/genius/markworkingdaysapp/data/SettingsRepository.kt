package com.genius.markworkingdaysapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.genius.markworkingdaysapp.model.AppSettings
import com.genius.markworkingdaysapp.model.ReminderSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.Locale

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun readSettings(): AppSettings {
        return AppSettings(
            dailyRate = dailyRate,
            currency = currency,
            firstDayOfWeek = firstDayOfWeek,
            reminder = ReminderSettings(
                enabled = notificationsEnabled,
                hour = reminderHour,
                minute = reminderMinute,
            )
        )
    }

    private fun updatePreferences(
        block: SharedPreferences.Editor.() -> Unit
    ) {
        prefs.edit(action = block)
        _settings.value = readSettings()
    }

    private val dailyRate: Int
        get() = prefs.getInt(KEY_DAILY_RATE, DEFAULT_DAILY_RATE)

    private val currency: String
        get() = prefs.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

    private val firstDayOfWeek: DayOfWeek
        get() {
            if (!prefs.contains(KEY_FIRST_DAY_OF_WEEK)) {
                return WeekFields
                    .of(Locale.getDefault())
                    .firstDayOfWeek
            }

            return FirstDayOfWeek.fromId(
                prefs.getInt(
                    KEY_FIRST_DAY_OF_WEEK,
                    FirstDayOfWeek.MONDAY.id
                )
            ).dayOfWeek
        }

    private val notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, DEFAULT_NOTIFICATIONS_ENABLED)

    private val reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)

    private val reminderMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)


    fun setDailyRate(value: Int) {
        require(value >= 0) {
            "Daily rate must not be negative: $value"
        }

        updatePreferences {
            putInt(KEY_DAILY_RATE, value)
        }
    }

    fun setCurrency(value: String) {
        updatePreferences {
            putString(KEY_CURRENCY, value)
        }
    }

    fun setFirstDayOfWeek(value: DayOfWeek) {
        val setting = FirstDayOfWeek.fromDayOfWeek(value)

        updatePreferences {
            putInt(KEY_FIRST_DAY_OF_WEEK, setting.id)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        updatePreferences {
            putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        require(hour in 0..23) {
            "Invalid reminder hour: $hour"
        }
        require(minute in 0..59) {
            "Invalid reminder minute: $minute"
        }

        updatePreferences {
            putInt(KEY_REMINDER_HOUR, hour)
            putInt(KEY_REMINDER_MINUTE, minute)
        }
    }


    private enum class FirstDayOfWeek(
        val id: Int,
        val dayOfWeek: DayOfWeek,
    ) {
        SUNDAY(
            id = 0,
            dayOfWeek = DayOfWeek.SUNDAY,
        ),
        MONDAY(
            id = 1,
            dayOfWeek = DayOfWeek.MONDAY,
        );

        companion object {
            fun fromId(id: Int): FirstDayOfWeek =
                entries.firstOrNull { it.id == id } ?: MONDAY

            fun fromDayOfWeek(value: DayOfWeek): FirstDayOfWeek =
                entries.firstOrNull { it.dayOfWeek == value }
                    ?: throw IllegalArgumentException(
                        "$value cannot be used as the first day of week"
                    )
        }
    }

    companion object {
        private const val PREFS_NAME = "app_settings"

        private const val KEY_DAILY_RATE = "daily_rate"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"

        private const val DEFAULT_DAILY_RATE = 100
        private const val DEFAULT_CURRENCY = "$"
        private const val DEFAULT_NOTIFICATIONS_ENABLED = false
        private const val DEFAULT_REMINDER_HOUR = 20
        private const val DEFAULT_REMINDER_MINUTE = 0
    }

}