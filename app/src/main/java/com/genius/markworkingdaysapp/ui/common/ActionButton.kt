package com.genius.markworkingdaysapp.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

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
            shape = CircleShape,
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