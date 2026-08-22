package com.genius.markworkingdaysapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.genius.markworkingdaysapp.ui.calendar.CalendarViewModel
import com.genius.markworkingdaysapp.ui.settings.SettingsViewModel
import com.genius.markworkingdaysapp.ui.statistics.StatisticsViewModel

object AppViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            CalendarViewModel(
                settingsRepository = application().settingsRepository,
                workDayRepository = application().workDayRepository,
            )
        }

        initializer {
            StatisticsViewModel(
                settingsRepository = application().settingsRepository
            )
        }

        initializer {
            SettingsViewModel(
                settingsRepository = application().settingsRepository
            )
        }

    }

}


private fun CreationExtras.application(): MarkWorkingDaysApplication {
    return this[
            ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
    ] as MarkWorkingDaysApplication
}