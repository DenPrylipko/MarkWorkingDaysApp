package com.genius.markworkingdaysapp.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.genius.markworkingdaysapp.AppViewModelProvider
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.model.MonthStatistics
import com.genius.markworkingdaysapp.ui.calendar.daycard.DayCard
import com.genius.markworkingdaysapp.ui.calendar.editdailyrate.EditDailyRateDialog
import com.genius.markworkingdaysapp.ui.calendar.editday.EditDayDialog
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.common.ActionButton
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.MonthItemUiState
import com.genius.markworkingdaysapp.ui.common.yearmonthdialog.YearMonthDialog
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import com.genius.markworkingdaysapp.ui.theme.appColors
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

const val DIALOG_FRACTION = 0.88f

@Composable
fun CalendarRoute(
    isEditDailyRateDialogVisible: Boolean,
    onEditDailyRateDialogDismiss: () -> Unit,
    viewModel: CalendarViewModel = viewModel<CalendarViewModel>(
        factory = AppViewModelProvider.Factory,
    ),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedDay by remember {
        mutableStateOf<DayCellUiModel?>(null)
    }

    CalendarScreen(
        uiState = uiState,
        onDayClick = { day ->
            selectedDay = day
        },
        onDisplayedMonthClick = {
            viewModel.onYearMonthDialogOpen()
        },
    )

    // EditDayDialog
    selectedDay?.let { day ->
        EditDayDialog(
            day = day,
            dailyRate = uiState.displayedMonthDailyRate,
            onSave = { workDay ->
                viewModel.onSaveDay(workDay)
                selectedDay = null
            },
            onDismissRequest = {
                selectedDay = null
            },
            modifier = Modifier.fillMaxWidth(DIALOG_FRACTION)
        )
    }

    // YearMonthDialog
    uiState.yearMonthDialogState?.let { state ->
        YearMonthDialog(
            state = state,
            displayedMonth = uiState.displayedMonthItem.yearMonth,
            onYearChanged = viewModel::onYearMonthDialogYearChanged,
            onMonthClick = {
                viewModel.onMonthSelected(it)
                viewModel.onYearMonthDialogYearDismiss()
            },
            onDismissRequest = {
                viewModel.onYearMonthDialogYearDismiss()
            },
            modifier = Modifier.fillMaxWidth(DIALOG_FRACTION)
        )
    }

    // EditDailyRateDialog
    if (isEditDailyRateDialogVisible) {
        EditDailyRateDialog(
            yearMonth = uiState.displayedMonthItem.yearMonth,
            incomingDailyRate = uiState.displayedMonthDailyRate,
            onSave = { newValue ->
                viewModel.onDailyRateSaved(newValue)
                onEditDailyRateDialogDismiss()
            },
            onDismiss = onEditDailyRateDialogDismiss,
            modifier = Modifier.fillMaxWidth(DIALOG_FRACTION),

        )
    }

}

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    onDayClick: (DayCellUiModel) -> Unit,
    onDisplayedMonthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    val currentDay = if (uiState.displayedMonthItem.yearMonth == YearMonth.from(today)) {
        uiState.days.firstOrNull { day ->
            day.date == today
        }
    } else {
        null
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.space12),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = AppSpacing.space18, horizontal = AppSpacing.space12
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space18)
        ) {

            MonthCalendar(
                displayedMonthItem = uiState.displayedMonthItem,
                daysOfWeek = uiState.daysOfWeek,
                monthGrid = uiState.days,
                onDayClick = onDayClick,
                onMonthClick = onDisplayedMonthClick,
            )

            currentDay?.let { day ->
                DayCard(
                    day = day, onDayClick = onDayClick, currencyLabel = uiState.currencyLabel
                )
            }

            MonthStatistics(statistics = uiState.monthStatistics)
        }
    }

}

@Composable
private fun MonthCalendar(
    displayedMonthItem: MonthItemUiState,
    daysOfWeek: List<DayOfWeek>,
    monthGrid: List<DayCellUiModel>,
    onDayClick: (DayCellUiModel) -> Unit,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthHeader = displayedMonthItem.yearMonth.getMonthTitle(withYear = true)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.space18)
    ) {

        ActionButton(
            label = monthHeader,
            onClick = onMonthClick,
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large
        ) {

            Column(
                modifier = Modifier.padding(AppSpacing.space12)
            ) {

                WeekdaysHeader(days = daysOfWeek)

                Spacer(Modifier.height(AppSpacing.space12))

                MonthGrid(
                    displayedMonth = displayedMonthItem.yearMonth,
                    days = monthGrid,
                    onDayClick = onDayClick,
                )

            }
        }
    }
}

@Composable
private fun WeekdaysHeader(
    days: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()

    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            Text(
                text = day.getDisplayName(
                    TextStyle.SHORT,
                    locale,
                ),
                style = if (day == today.dayOfWeek) {
                    MaterialTheme.typography.labelLarge
                } else {
                    MaterialTheme.typography.labelSmall
                },
                modifier = modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Composable
private fun MonthGrid(
    displayedMonth: YearMonth,
    days: List<DayCellUiModel>,
    onDayClick: (DayCellUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {

    val today = LocalDate.now()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.space6),
    ) {
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6),
            ) {
                week.forEach { day ->
                    DayCell(
                        displayedMonth = displayedMonth, today = today, day = day, onClick = {
                            onDayClick(day)
                        }, modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }

}

@Composable
private fun DayCell(
    displayedMonth: YearMonth,
    today: LocalDate,
    day: DayCellUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInDisplayedMonth = YearMonth.from(day.date) == displayedMonth
    val isToday = day.date == today
    val isFuture = day.date.isAfter(today)
    val isEnabled = isInDisplayedMonth && !isFuture
    val shouldHighlightToday = isToday && isInDisplayedMonth

    val containerColor = when (day.workDay?.status) {
        DayStatus.FULL_DAY -> MaterialTheme.appColors.calendarFullDay
        DayStatus.SHORT_DAY -> MaterialTheme.appColors.calendarShortDay
        DayStatus.NOT_WORKED, null -> MaterialTheme.appColors.calendarNotWorked
    }

    val contentColor = when (day.workDay?.status) {
        DayStatus.FULL_DAY -> MaterialTheme.appColors.calendarOnFullDay
        DayStatus.SHORT_DAY -> MaterialTheme.appColors.calendarOnShortDay
        DayStatus.NOT_WORKED, null -> MaterialTheme.appColors.calendarOnNotWorked
    }

    Surface(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.alpha(if (isInDisplayedMonth) 1f else 0.2f),
        shape = MaterialTheme.shapes.small,
        color = containerColor,
        contentColor = contentColor,
        border = if (shouldHighlightToday) {
            BorderStroke(
                width = AppDimensions.strokeDefault,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            null
        },
    ) {
        Box(
            modifier = Modifier
                .padding(AppSpacing.space3)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = if (shouldHighlightToday) MaterialTheme.colorScheme.primary else contentColor
            )

            day.workDay?.let { workDay ->

                val bottomText = if (workDay.status == DayStatus.SHORT_DAY) {
                    workDay.earned.toString()
                } else if (workDay.bonus != null) {
                    "+${workDay.bonus}"
                } else ""

                Text(
                    text = bottomText,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                if (workDay.note != null) {
                    Icon(
                        painter = painterResource(R.drawable.ic_comment),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(AppDimensions.dayCellNoteIcon),
                    )
                }

            }
        }
    }

}

@Composable
private fun MonthStatistics(
    statistics: MonthStatistics,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MonthStatisticsElement(
            value = statistics.workedDays,
            label = stringResource(R.string.month_statistics_working_days),
            modifier = Modifier.weight(1f),
        )
        MonthStatisticsElement(
            value = statistics.totalBonuses,
            label = stringResource(R.string.month_statistics_bonuses),
            modifier = Modifier.weight(1f),
        )
        MonthStatisticsElement(
            value = statistics.totalEarned,
            label = stringResource(R.string.month_statistics_total_earned),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MonthStatisticsElement(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Text(
                text = value.toString(), modifier = Modifier.padding(
                    horizontal = AppSpacing.space12,
                    vertical = AppSpacing.space6,
                ), style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
    }

}