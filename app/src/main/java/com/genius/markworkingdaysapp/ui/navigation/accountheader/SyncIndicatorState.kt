package com.genius.markworkingdaysapp.ui.navigation.accountheader

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.ui.theme.appColors

enum class SyncIndicatorState(
    @get:StringRes val textId: Int,
    @get:DrawableRes val iconId: Int,
) {
    SYNCED(
        textId = R.string.account_header_synced,
        iconId = R.drawable.ic_cloud_done,
    ),
    SYNCING(
        textId = R.string.account_header_syncing,
        iconId = R.drawable.ic_sync,
    );

    val contentColor: Color
        @Composable
        get() = when (this) {
            SYNCED -> MaterialTheme.appColors.accountSynced
            SYNCING -> MaterialTheme.appColors.accountSyncing
        }
}