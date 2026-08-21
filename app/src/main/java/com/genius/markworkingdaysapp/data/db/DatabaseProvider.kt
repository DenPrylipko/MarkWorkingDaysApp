package com.genius.markworkingdaysapp.data.db

import android.content.Context
import androidx.room.Room
import com.genius.markworkingdaysapp.data.db.migration.AppDatabaseMigration2To3
import com.genius.markworkingdaysapp.data.repository.SettingsRepository
import kotlin.concurrent.Volatile

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {

        return INSTANCE
            ?: synchronized(this) {
                INSTANCE ?: run {

                    val appContext = context.applicationContext
                    val settingsRepository = SettingsRepository(appContext)
                    val settings = settingsRepository.settings.value

                    Room.databaseBuilder(
                        appContext,
                        AppDatabase::class.java,
                        "work_day.db"
                    )
                        .addMigrations(AppDatabaseMigration2To3(settings.dailyRate))
                        .build()
                        .also { INSTANCE = it }
                }
            }
    }

}