package com.genius.markworkingdaysapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.genius.markworkingdaysapp.model.AppScreen
import com.genius.markworkingdaysapp.ui.calendar.CalendarRoute
import com.genius.markworkingdaysapp.ui.navigation.accountheader.AccountHeaderUiState
import com.genius.markworkingdaysapp.ui.theme.AppSpacing
import kotlinx.coroutines.launch

const val DRAWER_SCRIM_ALPHA = 0.5f
const val DRAWER_FRACTION = 0.7f

@Composable
fun MainScreen() {
    var selectedScreen by rememberSaveable {
        mutableStateOf(AppScreen.CALENDAR)
    }
    var isEditDailyRateDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isCalendarShareBottomSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
    )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = DRAWER_SCRIM_ALPHA),
        drawerContent = {
            NavigationDrawer(
                modifier = Modifier.fillMaxWidth(DRAWER_FRACTION),
                drawerState = drawerState,
                accountHeaderState = AccountHeaderUiState.SignedOut,
                onSignInClick = {},
                onSignOutClick = {},
                selectedScreen = selectedScreen,
                onScreenSelected = { newScreen ->
                    selectedScreen = newScreen
                },
            )
        },
    ) {

    Scaffold(
        topBar = {
            AppTopBar(
                selectedScreen = selectedScreen,
                onNavigationDrawerButtonClick = {
                    scope.launch {
                        drawerState.open()
                    }
                },
                onChangeRateClick = {
                    isEditDailyRateDialogVisible = true
                },
                onShareClick = {
                    isCalendarShareBottomSheetVisible = true
                },
            )
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = AppSpacing.space12),
        ) {
            when (selectedScreen) {
                AppScreen.CALENDAR -> CalendarRoute(
                    isEditDailyRateDialogVisible = isEditDailyRateDialogVisible,
                    onEditDailyRateDialogDismiss = {
                        isEditDailyRateDialogVisible = false
                    },
                    isCalendarShareBottomSheetVisible = isCalendarShareBottomSheetVisible,
                    onCalendarShareBottomSheetDismiss = {
                        isCalendarShareBottomSheetVisible = false
                    },
                )

                AppScreen.STATISTICS -> {}
                AppScreen.SETTINGS -> {}
            }
        }
    }
}

}