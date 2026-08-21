package com.genius.markworkingdaysapp.model

import java.time.DayOfWeek

data class AppSettings(
    val dailyRate: Int,
    val currencyLabel: String,
    val firstDayOfWeek: DayOfWeek,
    val reminder: ReminderSettings
)

data class ReminderSettings(
    val enabled: Boolean,
    val hour: Int,
    val minute: Int,
)
