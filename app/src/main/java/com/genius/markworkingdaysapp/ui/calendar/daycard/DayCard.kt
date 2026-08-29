package com.genius.markworkingdaysapp.ui.calendar.daycard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import com.genius.markworkingdaysapp.ui.theme.appColors


@Composable
internal fun DayCard(
    day: DayCellUiModel,
    onDayClick: (DayCellUiModel) -> Unit,
    currencyLabel: String,
    modifier: Modifier = Modifier,
) {

    DayCardContainer(
        day = day,
        onClick = onDayClick,
        modifier = modifier
    ) {
        if (day.workDay == null) {
            DayCardContentNoInfo(modifier = Modifier.matchParentSize())
        } else {
            DayCardContent(
                workDay = day.workDay,
                currencyLabel = currencyLabel,
                modifier = Modifier.matchParentSize(),
            )
        }
    }
}


@Composable
private fun DayCardContainer(
    day: DayCellUiModel,
    onClick: (DayCellUiModel) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {

    val hasEntry = day.hasEntry

    val containerColor = when (day.workDay?.status) {
        DayStatus.FULL_DAY -> MaterialTheme.appColors.statusFullDaySurface
        DayStatus.SHORT_DAY -> MaterialTheme.appColors.statusShortDaySurface
        DayStatus.NOT_WORKED -> MaterialTheme.appColors.statusNotWorkedSurface
        null -> MaterialTheme.colorScheme.primary
    }

    Surface(
        onClick = {
            onClick(day)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp),
        shape = MaterialTheme.shapes.large,
        color = containerColor,
        contentColor = if (hasEntry) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onPrimary
        },
        border = if (hasEntry) {
            BorderStroke(
                width = AppDimensions.strokeDefault,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            null
        },
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = AppSpacing.space24,
                vertical = AppSpacing.space18
            ),
        ) {
            content()

            Text(
                text = day.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.TopEnd),
                style = MaterialTheme.typography.headlineMedium,
                color = if (hasEntry) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        }
    }
}

