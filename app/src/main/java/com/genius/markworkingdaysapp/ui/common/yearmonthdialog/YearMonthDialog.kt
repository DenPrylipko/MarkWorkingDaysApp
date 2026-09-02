package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.ui.common.dialog.AppDialog
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

@Composable
fun YearMonthDialog(
    state: YearMonthDialogUiState,
    displayedMonth: YearMonth,
    onYearChanged: (Year) -> Unit,
    onMonthClick: (YearMonth) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.space24),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.space24)
            ) {
                YearSelector(
                    year = state.displayedYear.value,
                    onPreviousClick = { onYearChanged(state.displayedYear.minusYears(1)) },
                    onNextClick = { onYearChanged(state.displayedYear.plusYears(1)) },
                    modifier = Modifier.fillMaxWidth(),
                )

                MonthSelector(
                    monthItems = state.monthItems,
                    displayedMonth = displayedMonth,
                    onMonthClick = onMonthClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

}

@Composable
private fun YearSelector(
    year: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        val isCurrentYear = year == LocalDate.now().year

        YearSelectorButton(
            direction = YearDirection.PREVIOUS,
            onClick = onPreviousClick,
        )

        Text(
            text = year.toString(),
            style = MaterialTheme.typography.displayMedium,
            color = if (isCurrentYear)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )

        YearSelectorButton(
            direction = YearDirection.NEXT,
            onClick = onNextClick,
            enabled = !isCurrentYear
        )


    }

}

@Composable
private fun MonthSelector(
    monthItems: List<MonthItemUiState>,
    displayedMonth: YearMonth,
    onMonthClick: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
    ) {

        val currentMonth = YearMonth.now()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = AppSpacing.space24,
                    horizontal = AppSpacing.space18,
                ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space24),
        ) {
            monthItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.space12),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    rowItems.forEach { monthItem ->

                        MonthElement(
                            element = monthItem,
                            displayedMonth = displayedMonth,
                            onClick = onMonthClick,
                            modifier = Modifier.weight(1f),
                            enabled = monthItem.yearMonth <= currentMonth
                        )

                    }
                }

            }

        }

    }

}

@Composable
private fun MonthElement(
    element: MonthItemUiState,
    displayedMonth: YearMonth,
    onClick: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val style = element.status.getStyle()

    val containerAlpha = when {
        element.yearMonth == displayedMonth -> 1f
        enabled -> 0.7f
        else -> 0.15f
    }
    val contentAlpha = when {
        enabled -> 1f
        else -> 0.5f
    }

    Surface(
        modifier = modifier,
        onClick = {
            onClick(element.yearMonth)
        },
        enabled = enabled,
        shape = CircleShape,
        color = style.containerColor.copy(containerAlpha),
        contentColor = style.contentColor.copy(contentAlpha),
    ) {
        Box(
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = element.yearMonth.getMonthTitle(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = AppSpacing.space18)
            )
        }
    }

}

@Composable
private fun YearSelectorButton(
    direction: YearDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val shouldMirror = when (direction) {
        YearDirection.PREVIOUS -> isRtl
        YearDirection.NEXT -> !isRtl
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .size(AppDimensions.primaryIconButton),
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .size(AppDimensions.buttonIconContainer),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_left),
                        contentDescription = stringResource(direction.contentDescriptionRes),
                        modifier = Modifier
                            .scale(
                                scaleX = if (shouldMirror) -1f else 1f,
                                scaleY = 1f,
                            )
                            .size(AppDimensions.buttonIconDefault),
                    )
                }

            }
        }
    }

}

private enum class YearDirection(
    @param:StringRes
    @get:StringRes val contentDescriptionRes: Int,
) {
    PREVIOUS(R.string.button_previous_year_content_description),
    NEXT(R.string.button_next_year_content_description)
}
