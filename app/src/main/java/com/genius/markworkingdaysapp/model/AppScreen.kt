package com.genius.markworkingdaysapp.model

import androidx.annotation.StringRes
import com.genius.markworkingdaysapp.R

enum class AppScreen(
    @StringRes val titleRes: Int
) {
    CALENDAR(R.string.calendar_title),
    STATISTICS(R.string.statistics_title),
    SETTINGS(R.string.settings_title)
}