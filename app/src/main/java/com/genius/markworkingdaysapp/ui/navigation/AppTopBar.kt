package com.genius.markworkingdaysapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.AppScreen
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun AppTopBar(
    selectedScreen: AppScreen,
    modifier: Modifier = Modifier,
) {

    var isCalendarControlExpanded by remember(selectedScreen) {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(80.dp)
            .padding(
                horizontal = AppSpacing.space12,
                ),
    ) {

        PrimaryIconButton(
            icon = painterResource(R.drawable.ic_menu),
            contentDescription = stringResource(R.string.button_navigation_menu_content_description),
            onClick = {},
            modifier = Modifier.align(Alignment.CenterStart)
        )

        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space12),
        ) {

            val titleIcon = when (selectedScreen) {
                AppScreen.CALENDAR -> R.drawable.ic_calendar_today
                AppScreen.STATISTICS -> R.drawable.ic_statistics
                AppScreen.SETTINGS -> R.drawable.ic_settings
            }

            Icon(
                painter = painterResource(titleIcon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = stringResource(selectedScreen.titleRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }

        CalendarControl(
            isExpanded = isCalendarControlExpanded,
            onExpandedChange = { isCalendarControlExpanded = it },
            onChangeRateClick = {},
            onShareClick = {},
            modifier = Modifier.align(Alignment.CenterEnd)
        )


    }

}

@Composable
private fun PrimaryIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {

    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val iconContainerColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    Surface(
        onClick = onClick,
        modifier = modifier.size(AppDimensions.primaryIconButton),
        color = containerColor,
        shape = MaterialTheme.shapes.large,
    ) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .size(AppDimensions.buttonIconContainer),
                color = iconContainerColor,
                contentColor = iconColor,
                shape = CircleShape,
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(AppDimensions.buttonIconDefault),
                    )
                }

            }
        }

    }

}

@Composable
private fun SecondaryIconButton(
    icon: Painter,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {

        Column(
            modifier = Modifier
                .padding(
                    horizontal = AppSpacing.space3,
                    vertical = AppSpacing.space3,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space6)
        ) {

            Surface(
                modifier = Modifier.size(AppDimensions.buttonIconContainer),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
            ) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {

                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.buttonIconDefault)
                    )
                }

            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
            )

        }
    }
}

@Composable
private fun CalendarControl(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onChangeRateClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.space6),
    ) {
        if (isExpanded) {
            Surface(
                modifier = Modifier,
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large,
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = AppSpacing.space18,
                        vertical = AppSpacing.space6,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.space36),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    SecondaryIconButton(
                        icon = painterResource(R.drawable.ic_money),
                        label = stringResource(R.string.change_rate_icon_label),
                        onClick = onChangeRateClick,
                    )

                    SecondaryIconButton(
                        icon = painterResource(R.drawable.ic_share),
                        label = stringResource(R.string.share_icon_label),
                        onClick = onShareClick,
                    )

                }

            }
        }

        PrimaryIconButton(
            icon = painterResource(R.drawable.ic_more_horiz),
            contentDescription = stringResource(R.string.button_more_options_content_description),
            onClick = {
                onExpandedChange(!isExpanded)
            },
            isSelected = isExpanded
        )

    }


}