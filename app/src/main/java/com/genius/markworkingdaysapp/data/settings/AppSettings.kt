package com.genius.markworkingdaysapp.data.settings

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
        set(value) = prefs.edit {putString( KEY_CURRENCY, value) }

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
        private const val DEFAULT_DAILY_RATE = 450

        private const val KEY_FIRST_DAY_OF_WEEK = "first_day_of_week"
        private const val DEFAULT_FIRST_DAY_ID = 1 // MONDAY

        private const val KEY_CURRENCY = "currency"
        private const val DEFAULT_CURRENCY = ""


    }
}