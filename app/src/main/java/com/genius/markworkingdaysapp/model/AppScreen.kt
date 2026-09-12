package com.genius.markworkingdaysapp.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.genius.markworkingdaysapp.R

enum class AppScreen(
    @param:DrawableRes
    @get:DrawableRes
    val iconId: Int,
    @param:StringRes
    @get:StringRes
    val titleRes: Int
) {
    CALENDAR(iconId = R.drawable.ic_calendar_today, titleRes = R.string.calendar_title),
    STATISTICS(iconId = R.drawable.ic_statistics, titleRes = R.string.statistics_title),
    SETTINGS(iconId = R.drawable.ic_settings, titleRes = R.string.settings_title)
}