package com.genius.markworkingdaysapp.ui.calendar.editday

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import com.genius.markworkingdaysapp.ui.theme.appColors

@Composable
internal fun StatusSelector(
    status: DayStatus,
    onStatusChange: (DayStatus) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatusSelectorElement(
            label = stringResource(R.string.status_full_day),
            containerColor = MaterialTheme.appColors.statusFullDay,
            contentColor = MaterialTheme.appColors.statusOnFullDay,
            onClick = {
                onStatusChange(DayStatus.FULL_DAY)
            },
            selected = status == DayStatus.FULL_DAY,
        )
        StatusSelectorElement(
            label = stringResource(R.string.status_short_day),
            containerColor = MaterialTheme.appColors.statusShortDay,
            contentColor = MaterialTheme.appColors.statusOnShortDay,
            onClick = {
                onStatusChange(DayStatus.SHORT_DAY)
            },
            selected = status == DayStatus.SHORT_DAY,
        )
        StatusSelectorElement(
            label = stringResource(R.string.status_not_worked),
            containerColor = MaterialTheme.appColors.statusNotWorked,
            contentColor = MaterialTheme.appColors.statusOnNotWorked,
            onClick = {
                onStatusChange(DayStatus.NOT_WORKED)
            },
            selected = status == DayStatus.NOT_WORKED,
        )
    }
}

@Composable
private fun StatusSelectorElement(
    label: String,
    containerColor: Color,
    contentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val widthFraction = if (selected) 1f else 0.85f

    val displayedContainerColor = if (selected) {
        containerColor
    } else {
        containerColor.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .height(AppDimensions.statusSelectorTouchTarget)
            .padding(vertical = AppSpacing.space9)
            // Ripple stays inside the visual container
            // while Compose expands the hit target to 48.dp
            .clip(shape = MaterialTheme.shapes.medium)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .height(AppDimensions.statusSelectorContainer),
            shape = MaterialTheme.shapes.medium,
            color = displayedContainerColor,
            contentColor = contentColor,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = if (selected) {
                        MaterialTheme.typography.bodyLarge
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                    maxLines = 1,
                )
            }

        }
    }

}

