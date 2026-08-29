package com.genius.markworkingdaysapp.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.calendar.model.getStyle
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

            Box(
                modifier = Modifier.matchParentSize(),
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.space6)
                ) {

                    StatusRow(status = day.workDay.status)

                    Box(
                        modifier = Modifier.heightIn(min = 48.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.space6),
                        ) {

                            BonusRow(
                                bonus = day.workDay.bonus,
                                currencyLabel = currencyLabel,
                            )

                            NoteRow(
                                note = day.workDay.note,
                                modifier = Modifier
                            )

                        }
                    }

                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .heightIn(min = 55.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (day.workDay.status == DayStatus.NOT_WORKED) {

                        Icon(
                            painter = painterResource(R.drawable.ic_cloudy_night),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(AppSpacing.space6)
                                .size(AppDimensions.iconDefault),
                        )

                    } else {
                        TodayEarnedContainer(
                            earned = day.workDay.earned,
                            currencyLabel = currencyLabel,
                        )
                    }
                }
            }

        }

    }

}

@Composable
private fun StatusRow(
    status: DayStatus,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6)
    ) {

        if (status == DayStatus.FULL_DAY || status == DayStatus.SHORT_DAY) {
            Text(
                text = stringResource(R.string.day_card_worked),
                style = MaterialTheme.typography.titleMedium,
            )
            AttributeDot(size = AppDimensions.mediumAttributeDot)
        }

        Status(status = status)
    }

}

@Composable
private fun BonusRow(
    bonus: Int?,
    currencyLabel: String,
    modifier: Modifier = Modifier,
) {
    if (bonus != null) {

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6)
        ) {

            Text(
                text = stringResource(R.string.day_card_bonus),
                style = MaterialTheme.typography.labelLarge,
            )

            AttributeDot(size = AppDimensions.smallAttributeDot)

            Text(
                text = "$bonus $currencyLabel",
                style = MaterialTheme.typography.bodyMedium,
            )

        }

    }
}

@Composable
private fun NoteRow(
    note: String?,
    modifier: Modifier,
) {
    if (!note.isNullOrBlank()) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6)
        ) {
            Text(
                text = stringResource(R.string.day_card_note),
                style = MaterialTheme.typography.labelLarge,
            )
            AttributeDot(size = AppDimensions.smallAttributeDot)
            Text(
                text = "\"$note\"",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    } else {
        Text(
            text = stringResource(R.string.day_card_no_note),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun TodayEarnedContainer(
    earned: Int,
    currencyLabel: String,
    modifier: Modifier = Modifier,
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = AppSpacing.space18,
                vertical = AppSpacing.space9,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space9)
        ) {
            Text(
                text = stringResource(R.string.day_card_today_earned_label),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = "$earned $currencyLabel",
                style = MaterialTheme.typography.bodyLarge,
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

@Composable
private fun DayCardContentNoInfo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.TopStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6)
            ) {
                AttributeDot(AppDimensions.mediumAttributeDot)

                Text(
                    text = stringResource(R.string.day_card_no_info),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_touch),
                contentDescription = null,
                modifier = Modifier
                    .size(AppDimensions.dayCardTapToSetIcon)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.day_card_tap_to_set),
                style = MaterialTheme.typography.titleMedium,
            )
        }

    }
}

@Composable
private fun Status(
    status: DayStatus,
    modifier: Modifier = Modifier,
) {
    val style = status.getStyle()

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = style.containerColor,
        contentColor = style.contentColor,
    ) {
        Text(
            text = stringResource(style.textRes),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(
                horizontal = AppSpacing.space12,
                vertical = AppSpacing.space6
            ),
        )
    }
}

@Composable
private fun AttributeDot(
    size: Dp,
    color: Color = LocalContentColor.current,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = color,
                shape = CircleShape,
            ),
    )
}
