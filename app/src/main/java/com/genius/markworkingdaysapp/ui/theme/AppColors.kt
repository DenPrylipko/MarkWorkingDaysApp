package com.genius.markworkingdaysapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    // status
    val statusFullDay: Color,
    val statusOnFullDay: Color,
    val statusFullDaySurface: Color,

    val statusShortDay: Color,
    val statusOnShortDay: Color,
    val statusShortDaySurface: Color,

    val statusNotWorked: Color,
    val statusOnNotWorked: Color,
    val statusNotWorkedSurface: Color,

    // calendar
    val calendarFullDay: Color,
    val calendarOnFullDay: Color,

    val calendarShortDay: Color,
    val calendarOnShortDay: Color,

    val calendarNotWorked: Color,
    val calendarOnNotWorked: Color,

    // statistics
    val statisticsHighest: Color,
    val statisticAverage: Color,
    val statisticsLowest: Color,

    // account
    val accountSyncing: Color,
    val accountSynced: Color,

    // input
    val inputFocused: Color,
    val inputError: Color,
    )

internal val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors are not provided")
}

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current