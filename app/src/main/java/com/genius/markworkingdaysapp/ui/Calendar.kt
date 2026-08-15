package com.genius.markworkingdaysapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier.padding(
                vertical = AppSpacing.space18,
                horizontal = AppSpacing.space12
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space18)
        ) {
            MonthCalendar()
            DayCard()
            MonthStatistics()
        }
    }

}

@Composable
private fun MonthCalendar(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppSpacing.space18)
    ) {

        MonthHeader(
            monthTitle = "July 2026",
            onClick = {}
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.large
        ) {

            Column(
                modifier = Modifier.padding(AppSpacing.space12)
            ) {
                WeekdaysHeader()
                DaysGrid()
            }
        }
    }

}

@Composable
private fun MonthHeader(
    monthTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(40.dp)
            .widthIn(min = 120.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = MaterialTheme.shapes.large,
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = AppSpacing.space24
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

}

@Composable
private fun WeekdaysHeader() {

}

@Composable
private fun DaysGrid() {

}

@Composable
private fun DayCell() {

}

@Composable
private fun DayCard() {

}

@Composable
private fun MonthStatistics() {

    Row() {
        MonthStatisticsElement()
        MonthStatisticsElement()
        MonthStatisticsElement()
    }
}

@Composable
private fun MonthStatisticsElement() {

}