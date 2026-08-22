package com.genius.markworkingdaysapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.genius.markworkingdaysapp.ui.navigation.MainScreen
import com.genius.markworkingdaysapp.ui.theme.MarkWorkingDaysTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MarkWorkingDaysTheme {
                MainScreen()
            }
        }
    }
}