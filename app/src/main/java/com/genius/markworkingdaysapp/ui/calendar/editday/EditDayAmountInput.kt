package com.genius.markworkingdaysapp.ui.calendar.editday

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.DayStatus
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import com.genius.markworkingdaysapp.ui.theme.appColors

@Composable
internal fun AmountInput(
    type: AmountInputType,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(type.titleRes)
    val subtitle = stringResource(type.subtitleRes)
    val colors = type.colors()

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = colors.container,
        contentColor = colors.content,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = AppSpacing.space12,
                    horizontal = AppSpacing.space18,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.space3),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_money),
                        contentDescription = null,
                        modifier = Modifier.size(AppDimensions.iconDefault)
                    )

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,

                )

            Spacer(Modifier.height(AppSpacing.space12))

            AmountTextField(
                value = value,
                onValueChange = onValueChange,
            )
        }

    }

}

@Composable
private fun AmountTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxLength: Int = 6,
) {

    val focusManager = LocalFocusManager.current
    val textColor = MaterialTheme.colorScheme.onSurface

    var isFocused by remember {
        mutableStateOf(false)
    }


    Surface(
        modifier = modifier.size(
            width = AppDimensions.amountTextFieldWidth,
            height = AppDimensions.amountTextFieldHeight,
        ),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (
                    newValue.length <= maxLength &&
                    newValue.all(Char::isDigit)
                ) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { focusState ->
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
                        .fillMaxSize()
                        .padding(horizontal = AppSpacing.space18),
                    contentAlignment = Alignment.Center,
                ) {
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = "0",
                            color = textColor.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    innerTextField()
                }
            },
        )

    }
}


internal enum class AmountInputType(
    @get:StringRes val titleRes: Int,
    @get:StringRes val subtitleRes: Int,
) {
    BONUS(
        titleRes = R.string.edit_day_bonus,
        subtitleRes = R.string.edit_day_bonus_optional,
    ),
    EARNED(
        titleRes = R.string.edit_day_earned,
        subtitleRes = R.string.edit_day_earned_required,
    ),
}


internal val DayStatus.amountInputType: AmountInputType?
    get() = when (this) {
        DayStatus.FULL_DAY -> AmountInputType.BONUS
        DayStatus.SHORT_DAY -> AmountInputType.EARNED
        DayStatus.NOT_WORKED -> null
    }


private data class AmountInputColors(
    val container: Color,
    val content: Color,
)

@Composable
private fun AmountInputType.colors(): AmountInputColors =
    when (this) {
        AmountInputType.BONUS -> AmountInputColors(
            container = MaterialTheme.appColors.statusFullDay,
            content = MaterialTheme.appColors.statusOnFullDay,
        )

        AmountInputType.EARNED -> AmountInputColors(
            container = MaterialTheme.appColors.statusShortDay,
            content = MaterialTheme.appColors.statusOnShortDay,
        )
    }

