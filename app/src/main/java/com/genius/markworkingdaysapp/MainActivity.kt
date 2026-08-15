package com.genius.markworkingdaysapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.genius.markworkingdaysapp.ui.theme.MarkWorkingDaysTheme
import com.genius.markworkingdaysapp.ui.theme.appColors
import com.genius.markworkingdaysapp.ui.xml.MainXmlScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            MarkWorkingDaysTheme {
                MainXmlScreen()
            }
        }
    }
}