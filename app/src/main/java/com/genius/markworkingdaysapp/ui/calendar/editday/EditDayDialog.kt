package com.genius.markworkingdaysapp.ui.calendar.editday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.model.WorkDay
import com.genius.markworkingdaysapp.ui.calendar.model.DayCellUiModel
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import com.genius.markworkingdaysapp.ui.theme.appColors
import java.time.LocalDate

@Composable
fun EditDayDialog(
    day: DayCellUiModel,
    dailyRate: Int,
    onSave: (WorkDay) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val workDay = day.workDay

    var selectedStatus by rememberSaveable {
        mutableStateOf(
            workDay?.status ?: DayStatus.NOT_WORKED
        )
    }

    var bonus by rememberSaveable {
        mutableStateOf(
            workDay?.bonus?.toString().orEmpty()
        )
    }

    var earned by rememberSaveable {
        mutableStateOf(
            if (day.workDay?.status == DayStatus.SHORT_DAY)
                workDay.earned.toString()
            else ""
        )
    }

    var note by rememberSaveable {
        mutableStateOf(
            workDay?.note.orEmpty()
        )
    }

    val surfaceColor = when (selectedStatus) {
        DayStatus.FULL_DAY -> MaterialTheme.appColors.statusFullDaySurface
        DayStatus.SHORT_DAY -> MaterialTheme.appColors.statusShortDaySurface
        DayStatus.NOT_WORKED -> MaterialTheme.appColors.statusNotWorkedSurface
    }


    Dialog(
        onDismissRequest = onDismissRequest,
    ) {

        val today = LocalDate.now()

        val dateColor = if (day.date == today) {
            MaterialTheme.colorScheme.primary
        } else when (selectedStatus) {
            DayStatus.FULL_DAY -> MaterialTheme.appColors.statusFullDay
            DayStatus.SHORT_DAY -> MaterialTheme.appColors.statusShortDay
            DayStatus.NOT_WORKED -> MaterialTheme.appColors.statusNotWorked
        }

        val isSaveEnabled = (earned.toIntOrNull() != null && earned.toIntOrNull() != 0) ||
                selectedStatus != DayStatus.SHORT_DAY

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = surfaceColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.space18),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.space24),
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    StatusSelector(
                        status = selectedStatus,
                        onStatusChange = { newStatus ->
                            selectedStatus = newStatus
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = day.date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.weight(1f),
                        color = dateColor,
                        textAlign = TextAlign.Center

                    )

                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedStatus == DayStatus.NOT_WORKED) {

                        val contentColor = MaterialTheme.appColors.statusNotWorked

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cloudy_night),
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(AppDimensions.iconDefault)
                            )
                            Text(
                                text = stringResource(R.string.edit_day_day_off),
                                style = MaterialTheme.typography.titleLarge,
                                color = contentColor,
                            )

                        }
                    } else {
                        selectedStatus.amountInputType?.let { fieldType ->
                            AmountInput(
                                type = fieldType,
                                value = when (fieldType) {
                                    AmountInputType.BONUS -> bonus
                                    AmountInputType.EARNED -> earned
                                },
                                onValueChange = {
                                    when (fieldType) {
                                        AmountInputType.BONUS -> bonus = it
                                        AmountInputType.EARNED -> earned = it
                                    }
                                }
                            )
                        }
                    }
                }

                NoteInput(
                    text = note,
                    onTextChange = { newText ->
                        note = newText
                    }
                )

                PrimaryButton(
                    label = stringResource(R.string.edit_day_action_save),
                    onClick = {
                        val selectedBonus = if (selectedStatus == DayStatus.FULL_DAY)
                            bonus.toIntOrNull()
                        else null
                        val dayEarned = when (selectedStatus) {
                            DayStatus.FULL_DAY -> dailyRate + (selectedBonus ?: 0)
                            DayStatus.SHORT_DAY -> earned.toInt()
                            DayStatus.NOT_WORKED -> 0
                        }

                        val collectedWorkDay = WorkDay(
                            date = day.date,
                            status = selectedStatus,
                            bonus = selectedBonus,
                            earned = dayEarned,
                            note = note.ifBlank { null },
                        )

                        onSave(collectedWorkDay)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = isSaveEnabled
                )


            }

        }
    }

}


@Composable
private fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = MaterialTheme.shapes.large
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    val textStyle = if (enabled)
        MaterialTheme.typography.titleLarge
    else
        MaterialTheme.typography.bodyMedium


    Box(
        modifier = modifier.height(50.dp)
    ) {

        Surface(
            modifier = Modifier,
            enabled = enabled,
            onClick = onClick,
            shape = shape,
            color = if (enabled)
                containerColor
            else
                containerColor.copy(alpha = 0.3f),
            contentColor = contentColor,
        ) {

            Text(
                text = label,
                style = textStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    horizontal = AppSpacing.space24,
                    vertical = AppSpacing.space12
                ),
            )

        }
    }
}
