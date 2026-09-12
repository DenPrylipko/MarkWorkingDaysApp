package com.genius.markworkingdaysapp.ui.navigation.accountheader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun AccountHeader(
    state: AccountHeaderUiState,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        AccountHeaderUiState.SignedOut -> {
            SignedOutHeader(
                onClick = onSignIn,
                modifier = modifier,
            )
        }

        AccountHeaderUiState.SigningIn -> {
            SignedOutHeader(
                onClick = onSignIn,
                enabled = false,
                modifier = modifier,
            )
        }

        is AccountHeaderUiState.SignedIn -> {
           SignedInHeader(
               stateSignedIn = state,
               onSignOutClick = onSignOut,
               modifier = modifier,
               )
        }
    }

}

@Composable
private fun SignedOutHeader(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics { role = Role.Button }
            .alpha(if (enabled) 1f else 0.5f),
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.space9),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = AppSpacing.space9,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            Text(
                text = stringResource(R.string.account_header_log_in),
                style = MaterialTheme.typography.titleLarge,
            )

            Icon(
                painter = painterResource(R.drawable.ic_cloud_sync),
                contentDescription = null,
            )

        }

    }

}

@Composable
private fun SignedInHeader(
    stateSignedIn: AccountHeaderUiState.SignedIn,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    vertical = AppSpacing.space9,
                    horizontal = AppSpacing.space18,
                    ),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space24),
        ) {
            AccountInfo(
                username = stateSignedIn.displayName,
                mail = stateSignedIn.email,
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SignOutButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.align(Alignment.CenterStart),
                )

                SyncIndicator(
                    state = stateSignedIn.syncState,
                    modifier = Modifier.align(Alignment.BottomEnd),
                )

            }

        }

    }

}

@Composable
private fun AccountInfo(
    username: String?,
    mail: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = username ?: "",
            style = MaterialTheme.typography.bodyLarge,
        )

        Text(
            text = mail ?: "",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SignOutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .semantics { role = Role.Button },
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = AppSpacing.space9,
                    horizontal = AppSpacing.space18,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.space9),
        ) {

            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
            )

            Text(
                text = stringResource(R.string.account_header_log_out),
                style = MaterialTheme.typography.bodyLarge,
            )

        }

    }

}

@Composable
private fun SyncIndicator(
    state: SyncIndicatorState,
    modifier: Modifier = Modifier,
) {
    val contentColor = state.contentColor

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.space9),
    ) {

        Text(
            text = stringResource(state.textId),
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
        )

        Icon(
            painter = painterResource(state.iconId),
            contentDescription = null,
            tint = contentColor
        )

    }


}

