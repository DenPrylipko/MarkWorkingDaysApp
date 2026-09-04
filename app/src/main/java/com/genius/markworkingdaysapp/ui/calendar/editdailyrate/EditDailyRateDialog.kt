package com.genius.markworkingdaysapp.ui.calendar.editdailyrate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.ui.common.ActionButton
import com.genius.markworkingdaysapp.ui.common.dialog.AppDialog
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import java.time.YearMonth

@Composable
fun EditDailyRateDialog(
    yearMonth: YearMonth,
    incomingDailyRate: Int,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    var dailyRateState by rememberSaveable {
        mutableStateOf(incomingDailyRate.toString())
    }

    AppDialog(
        onDismissRequest = onDismiss,
    ) {

        Surface(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            Column(
                modifier = Modifier
                    .padding(
                        vertical = AppSpacing.space24,
                        horizontal = AppSpacing.space24
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.space12)
            ) {
                TitleRow()

                Content(
                    yearMonth = yearMonth,
                    dailyRate = dailyRateState,
                    onDailyRateChange = {
                        dailyRateState = it
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ActionButton(
                    label = stringResource(R.string.change_rate_action_save),
                    onClick = {
                        onSave(dailyRateState.toIntOrNull() ?: 0)
                    },
                    enabled = dailyRateState.toIntOrNull() != null &&
                            dailyRateState.toIntOrNull() != 0
                )

                Description(month = yearMonth)
            }


        }

    }

}


@Composable
private fun TitleRow(
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space9),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_money),
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.iconDefault),
            )

            Text(
                text = stringResource(R.string.change_rate_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

}

@Composable
private fun Content(
    yearMonth: YearMonth,
    dailyRate: String,
    onDailyRateChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(vertical = AppSpacing.space18),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space12),
        ) {
            Text(
                text = yearMonth.getMonthTitle(withYear = true),
                style = MaterialTheme.typography.titleMedium,
            )

            TextField(
                value = dailyRate,
                onValueChange = onDailyRateChange
            )
        }

    }

}

@Composable
private fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val focusManager = LocalFocusManager.current
    val textColor = MaterialTheme.colorScheme.onSurface

    var isFocused by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.all(Char::isDigit)) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = textColor,
                textAlign = TextAlign.Center,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                },
            ),
            cursorBrush = SolidColor(
                MaterialTheme.colorScheme.primary,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(
                            horizontal = AppSpacing.space24,
                            vertical = AppSpacing.space12,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = "0",
                            color = textColor.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    innerTextField()

                }
            }
        )
    }
}

@Composable
private fun Description(
    month: YearMonth,
    modifier: Modifier = Modifier,
) {
    val text = stringResource(
        R.string.change_rate_description,
        month.getMonthTitle()
    )

    val iconId = "info_icon"
    val contentColor = LocalContentColor.current

    val annotatedText = buildAnnotatedString {
        appendInlineContent(
            id = iconId,
            alternateText = "[info]"
        )
        append(text)
    }

    val inlineContent = mapOf(
        iconId to InlineTextContent(
            placeholder = Placeholder(
                width = 32.sp,
                height = 24.sp,
                placeholderVerticalAlign =
                    PlaceholderVerticalAlign.TextCenter,
            ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    tint = contentColor,
                )
            }
        },
    )

    Text(
        text = annotatedText,
        inlineContent = inlineContent,
        modifier = modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelLarge,
        color = contentColor,
        textAlign = TextAlign.Center,
    )
}

