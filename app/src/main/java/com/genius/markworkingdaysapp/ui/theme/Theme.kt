package com.genius.markworkingdaysapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = NeutralWhite,

    background = Neutral250,
    onBackground = Neutral900,

    surface = Neutral100,
    onSurface = Neutral800,

    surfaceVariant = NeutralWhite,
    onSurfaceVariant = Neutral800,

    outline = Neutral700,
    error = Red100,
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue300,
    onPrimary = NeutralWhite,

    background = Neutral900,
    onBackground = NeutralWhite,

    surface = Neutral800,
    onSurface = NeutralWhite,

    surfaceVariant = Neutral700,
    onSurfaceVariant = NeutralWhite,

    outline = NeutralWhite,
    error = Red900,
)

private val LightAppColors = AppColors(
    // status
    statusFullDay = Green400,
    statusOnFullDay = NeutralWhite,
    statusFullDaySurface = Green50,

    statusShortDay = Orange400,
    statusOnShortDay = NeutralWhite,
    statusShortDaySurface = Orange50,

    statusNotWorked = Neutral400,
    statusOnNotWorked = NeutralWhite,
    statusNotWorkedSurface = Neutral50,

    // calendar
    calendarFullDay = Green150,
    calendarOnFullDay = Neutral700,

    calendarShortDay = Orange150,
    calendarOnShortDay = Neutral700,

    calendarNotWorked = Neutral150,
    calendarOnNotWorked = Neutral700,

    // calendar month
    calendarMonthWorked = Green400,
    calendarMonthOnWorked = NeutralWhite,

    calendarMonthDefault = Neutral400,
    calendarMonthOnDefault = NeutralWhite,

    // statistics
    statisticsHighest = Green250,
    statisticAverage = Blue250,
    statisticsLowest = Orange250,

    // account
    accountSyncing = Orange100,
    accountSynced = Green100,

    // input
    inputFocused = Blue100,
    inputError = Red100,
)

private val DarkAppColors = AppColors(
    // status
    statusFullDay = Green500,
    statusOnFullDay = NeutralWhite,
    statusFullDaySurface = Green900,

    statusShortDay = Orange500,
    statusOnShortDay = NeutralWhite,
    statusShortDaySurface = Orange900,

    statusNotWorked = Neutral500,
    statusOnNotWorked = NeutralWhite,
    statusNotWorkedSurface = Neutral900,

    // calendar
    calendarFullDay = Green800,
    calendarOnFullDay = NeutralWhite,

    calendarShortDay = Orange800,
    calendarOnShortDay = NeutralWhite,

    calendarNotWorked = Neutral800,
    calendarOnNotWorked = NeutralWhite,

    // calendar month
    calendarMonthWorked = Green500,
    calendarMonthOnWorked = NeutralWhite,

    calendarMonthDefault = Neutral500,
    calendarMonthOnDefault = NeutralWhite,

    // statistics
    statisticsHighest = Green800,
    statisticAverage = Blue800,
    statisticsLowest = Orange800,

    // account
    accountSyncing = Orange100,
    accountSynced = Green100,

    // input
    inputFocused = Blue900,
    inputError = Red900,
)

@Composable
fun MarkWorkingDaysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val appColors = if (darkTheme) {
        DarkAppColors
    } else {
        LightAppColors
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}