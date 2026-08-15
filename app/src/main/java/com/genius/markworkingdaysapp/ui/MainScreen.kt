package com.genius.markworkingdaysapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.genius.markworkingdaysapp.ui.theme.AppSpacing

@Composable
fun MainScreen(

) {
    Scaffold(
        topBar = {
            AppTopBar()
        }

    ) { innerPadding ->
        CalendarScreen(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = AppSpacing.space12)
        )
        
    }

}