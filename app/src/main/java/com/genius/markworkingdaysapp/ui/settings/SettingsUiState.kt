package com.genius.markworkingdaysapp.ui.settings

import java.time.DayOfWeek

data class SettingsUiState(
    val dailyRate: Int,
    val currency: String,
    val firstDayOfWeek: DayOfWeek,
    val notificationsEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val todayChecked: Boolean
) {
}