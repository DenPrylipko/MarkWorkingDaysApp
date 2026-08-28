package com.genius.markworkingdaysapp.ui.calendar.editday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing


@Composable
internal fun NoteInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val title = stringResource(R.string.edit_day_note)
    val icon = painterResource(R.drawable.ic_edit)
    val shape = MaterialTheme.shapes.large
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppSpacing.space18,
                    vertical = AppSpacing.space12,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.space3),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimensions.iconDefault)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Spacer(Modifier.height(AppSpacing.space12))

            NoteTextField(
                text = text,
                onTextChange = onTextChange,
            )


        }

    }
}

@Composable
private fun NoteTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current
    val textColor = MaterialTheme.colorScheme.onSurface
    val shape = MaterialTheme.shapes.medium
    val containerColor = MaterialTheme.colorScheme.surface
    val hintText = stringResource(R.string.edit_day_note_hint)

    var isFocused by remember {
        mutableStateOf(false)
    }


    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text ->
                onTextChange(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .padding(
                    horizontal = AppSpacing.space12,
                    vertical = AppSpacing.space6,
                ),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = textColor,
                textAlign = TextAlign.Center
            ),
            minLines = 1,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
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
                    contentAlignment = Alignment.Center,
                ) {
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            text = hintText,
                            color = textColor.copy(alpha = 0.5f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    innerTextField()
                }
            }

        )

    }

}