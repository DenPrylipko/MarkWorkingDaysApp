package com.genius.markworkingdaysapp.model

import androidx.annotation.StringRes
import com.genius.markworkingdaysapp.R

enum class MainScreen(
    @StringRes val titleRes: Int
) {
    Calendar(R.string.calendar_title),
    Statistics(R.string.statistics_title),
    Settings(R.string.settings_title)
}