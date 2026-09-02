package com.genius.markworkingdaysapp.ui.calendar.model

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.ui.theme.appColors

data class DayStatusStyle(
    val containerColor: Color,
    val contentColor: Color,
    @param:StringRes
    @get:StringRes
    val textRes: Int,
)

@Composable
fun DayStatus.getStyle(): DayStatusStyle {

    val appColors = MaterialTheme.appColors

    return when (this) {
        DayStatus.FULL_DAY -> DayStatusStyle(
                containerColor = appColors.statusFullDay,
            contentColor = appColors.statusOnFullDay,
            textRes = R.string.status_full_day,
        )
        DayStatus.SHORT_DAY -> DayStatusStyle(
            containerColor = appColors.statusShortDay,
            contentColor = appColors.statusOnShortDay,
            textRes = R.string.status_short_day,
        )
        DayStatus.NOT_WORKED -> DayStatusStyle(
            containerColor = appColors.statusNotWorked,
            contentColor = appColors.statusOnNotWorked,
            textRes = R.string.status_not_worked,
        )
    }
}

