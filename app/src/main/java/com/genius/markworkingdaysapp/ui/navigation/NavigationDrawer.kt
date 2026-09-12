package com.genius.markworkingdaysapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.genius.markworkingdaysapp.BuildConfig
import com.genius.markworkingdaysapp.R
import com.genius.markworkingdaysapp.model.AppScreen
import com.genius.markworkingdaysapp.ui.navigation.accountheader.AccountHeader
import com.genius.markworkingdaysapp.ui.navigation.accountheader.AccountHeaderUiState
import com.genius.markworkingdaysapp.ui.theme.AppDimensions
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun NavigationDrawer(
    drawerState: DrawerState,
    accountHeaderState: AccountHeaderUiState,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    selectedScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        drawerState = drawerState,
        modifier = modifier,
        drawerContainerColor = MaterialTheme.colorScheme.background,
        drawerContentColor = MaterialTheme.colorScheme.onBackground,
        drawerShape = MaterialTheme.shapes.large,
        drawerTonalElevation = 0.dp,
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = AppSpacing.space48,
                    horizontal = AppSpacing.space24,
                ),
        ) {

            AccountHeader(
                state = accountHeaderState,
                onSignIn = onSignInClick,
                onSignOut = onSignOutClick,
                modifier = Modifier.align(Alignment.TopCenter),
            )

            ScreenChooserPanel(
                selectedScreen = selectedScreen,
                onScreenItemClick = onScreenSelected,
                modifier = Modifier.align(Alignment.Center),
            )

            AppVersion(
                modifier = Modifier.align(Alignment.BottomCenter),
            )

        }

    }


}


@Composable
private fun ScreenChooserPanel(
    selectedScreen: AppScreen,
    onScreenItemClick: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(
                vertical = AppSpacing.space24,
                horizontal = AppSpacing.space12
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.space48)
        ) {

            ScreenChooserItem(
                screen = AppScreen.CALENDAR,
                selected = selectedScreen == AppScreen.CALENDAR,
                onClick = onScreenItemClick,
            )

            ScreenChooserItem(
                screen = AppScreen.STATISTICS,
                selected = selectedScreen == AppScreen.STATISTICS,
                onClick = onScreenItemClick,
            )

            ScreenChooserItem(
                screen = AppScreen.SETTINGS,
                selected = selectedScreen == AppScreen.SETTINGS,
                onClick = onScreenItemClick,
            )

        }
    }
}

@Composable
private fun ScreenChooserItem(
    screen: AppScreen,
    selected: Boolean,
    onClick: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val (containerColor, contentColor) = if (selected) {
        colorScheme.primary to colorScheme.onPrimary
    } else {
        colorScheme.surfaceVariant to colorScheme.onSurface
    }

    Surface(
        selected = selected,
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        onClick = {
            onClick(screen)
        },
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(vertical = AppSpacing.space18),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = AppSpacing.space9,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            Icon(
                painter = painterResource(screen.iconId),
                contentDescription = null,
                modifier = Modifier.size(AppDimensions.iconDefault)
            )

            Text(
                text = stringResource(screen.titleRes),
                style = MaterialTheme.typography.titleMedium,
            )

        }

    }

}

@Composable
private fun AppVersion(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.version_format, BuildConfig.VERSION_NAME),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
    )
}