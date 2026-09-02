package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.genius.markworkingdaysapp.model.MonthStatus
import com.genius.markworkingdaysapp.ui.theme.appColors

internal object MonthItemDefaults {
    const val SELECTED_CONTAINER_ALPHA = 1f
    const val ENABLED_CONTAINER_ALPHA = 0.7f
    const val DISABLED_CONTAINER_ALPHA = 0.15f

    const val ENABLED_CONTENT_ALPHA = 1f
    const val DISABLED_CONTENT_ALPHA = 0.5f
}

data class MonthStatusStyle(
    val containerColor: Color,
    val contentColor: Color,
)

@Composable
fun MonthStatus.getStyle(): MonthStatusStyle {
    return  when (this) {
        MonthStatus.CURRENT -> MonthStatusStyle(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
        MonthStatus.PAST_WORKED -> MonthStatusStyle(
            containerColor = MaterialTheme.appColors.calendarMonthWorked,
            contentColor = MaterialTheme.appColors.calendarMonthOnWorked,
        )
        MonthStatus.PAST_NOT_WORKED, MonthStatus.FUTURE -> MonthStatusStyle(
            containerColor = MaterialTheme.appColors.calendarMonthDefault,
            contentColor = MaterialTheme.appColors.calendarMonthOnDefault,
        )
    }
}