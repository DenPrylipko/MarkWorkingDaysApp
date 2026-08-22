package com.genius.markworkingdaysapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.genius.markworkingdaysapp.model.AppScreen
import com.genius.markworkingdaysapp.model.AppScreen.CALENDAR
import com.genius.markworkingdaysapp.ui.calendar.CalendarRoute

@Composable
fun MainScreen() {
    var selectedScreen by rememberSaveable {
        mutableStateOf(CALENDAR)
    }

    Scaffold(
        topBar = {
            AppTopBar()
        },
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (selectedScreen) {
                AppScreen.CALENDAR -> CalendarRoute()
                AppScreen.STATISTICS -> {}
                AppScreen.SETTINGS -> {}
            }
        }
    }

}