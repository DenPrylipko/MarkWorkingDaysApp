package com.genius.markworkingdaysapp.data

import android.content.Context
import androidx.core.content.edit

class AppSettings(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var dailyRate: Int
        get() = prefs.getInt(KEY_DAILY_RATE, DEFAULT_DAILY_RATE)
        set(value) = prefs.edit() { putInt(KEY_DAILY_RATE, value) }

    var firstDayOfWeek: FirstDayOfWeek
        get() = FirstDayOfWeek.fromId(
            prefs.getInt(KEY_FIRST_DAY_OF_WEEK, DEFAULT_FIRST_DAY_ID)
        )
        set(value) = prefs.edit { putInt(KEY_FIRST_DAY_OF_WEEK, value.id) }

    var currency: String
        get() = prefs.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: ""
        set(value) = prefs.edit { putString( KEY_CURRENCY, value) }

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, DEFAULT_NOTIFICATIONS_ENABLED)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }

    var reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
        set(value) = prefs.edit { putInt(KEY_REMINDER_HOUR, value) }

    var reminderMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
        set(value) = prefs.edit { putInt(KEY_REMINDER_MINUTE, value) }

    var todayChecked: Boolean
        get() = prefs.getBoolean(KEY_TODAY_CHECKED, DEFAULT_TODAY_CHECKED)
        set(value) = prefs.edit { putBoolean(KEY_TODAY_CHECKED, value)}


    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit {
            putInt(KEY_REMINDER_HOUR, hour)
            putInt(KEY_REMINDER_MINUTE, minute)
        }
    }


    enum class FirstDayOfWeek(val id: Int) {
        SUNDAY(0),
        MONDAY(1);

        companion object {
            fun fromId(id: Int) : FirstDayOfWeek =
                entries.firstOrNull { it.id == id } ?: MONDAY
        }
    }

    companion object {
        private const val PREFS_NAME = "app_settings"

        private const val KEY_DAILY_RATE = "daily_rate"
        private const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_TODAY_CHECKED = "today_checked"

        private const val DEFAULT_DAILY_RATE = 450
        private const val DEFAULT_FIRST_DAY_ID = 1 // MONDAY
        private const val DEFAULT_CURRENCY = ""
        private const val DEFAULT_NOTIFICATIONS_ENABLED = false
        private const val DEFAULT_REMINDER_HOUR = 20
        private const val DEFAULT_REMINDER_MINUTE = 0
        private const val DEFAULT_TODAY_CHECKED = false


    }
}