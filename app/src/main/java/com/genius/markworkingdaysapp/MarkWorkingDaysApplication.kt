package com.genius.markworkingdaysapp

import android.app.Application
import com.genius.markworkingdaysapp.data.db.AppDatabase
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.repository.SettingsRepository
import com.genius.markworkingdaysapp.data.repository.WorkDayRepository

class MarkWorkingDaysApplication : Application() {

    val database: AppDatabase by lazy {
        DatabaseProvider.get(this)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }

    val workDayRepository: WorkDayRepository by lazy {
        WorkDayRepository(database)
    }
}