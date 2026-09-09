package com.genius.markworkingdaysapp.ui.calendar.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarShareBottomSheet(
    calendarPreview: ImageBitmap?,
    shareText: String,
    onShareImage: () -> Unit,
    onShareText: () -> Unit,
    onShareImageAndText: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        CalendarShareContent(
            calendarPreview = calendarPreview,
            shareText = shareText,
            onShareImage = onShareImage,
            onShareText = onShareText,
            onShareImageAndText = onShareImageAndText,
        )

    }

}

@Composable
fun CalendarShareContent(
    calendarPreview: ImageBitmap?,
    shareText: String,
    onShareImage: () -> Unit,
    onShareText: () -> Unit,
    onShareImageAndText: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(
                vertical = AppSpacing.space24,
                horizontal = AppSpacing.space12,
            ),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.space24),
    ) {

        ShareTitle(
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            modifier = Modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.space24),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.space24),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.space24),
                ) {
                    ShareOption(
                        label = stringResource(R.string.share_image_label),
                        enabled = calendarPreview != null,
                        onClick = onShareImage,
                        modifier = Modifier.weight(1f)
                    ) {
                        CalendarImagePreview(
                            image = calendarPreview,
                        )
                    }

                    ShareOption(
                        label = stringResource(R.string.share_text_label),
                        onClick = onShareText,
                        modifier = Modifier.weight(1f)
                    ) {
                        CalendarTextPreview(
                            text = shareText,
                        )
                    }

                }

                ShareButton(
                    label = stringResource(R.string.share_image_and_text_label),
                    onClick = onShareImageAndText,
                    enabled = calendarPreview != null,
                    modifier = Modifier.fillMaxWidth(),
                )

            }

        }

    }

}

@Composable
private fun ShareTitle(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_share),
            contentDescription = null,
            modifier = Modifier,
        )

        Spacer(Modifier.width(AppSpacing.space9))

        Text(
            text = stringResource(R.string.share_title),
            style = MaterialTheme.typography.headlineMedium,
        )

    }
}

@Composable
private fun ShareOption(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    preview: @Composable BoxScope?.() -> Unit,
) {

    Surface(
        onClick = onClick,
        modifier = modifier.height(216.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = AppSpacing.space12,
                horizontal = AppSpacing.space24,
                ),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space12),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                Box(
                    modifier = Modifier.padding(AppSpacing.space9),
                    contentAlignment = Alignment.Center,
                    content = preview,
                )
            }

            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                )

        }

    }

}

@Composable
private fun ShareButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {

    Surface(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = AppSpacing.space12)
        )

    }

}

@Composable
fun CalendarImagePreview(
    image: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            Image(
                bitmap = image,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
            )
        }
    }

}

@Composable
fun CalendarTextPreview(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxSize(),
        fontSize = 6.sp,
        lineHeight = 7.sp,
        overflow = TextOverflow.Clip,
    )

}

