package com.genius.markworkingdaysapp.data.db

import android.content.Context
import androidx.room.Room
import kotlin.concurrent.Volatile

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context) : AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "work_day.db")
                .build().also { INSTANCE = it}
        }
    }

}