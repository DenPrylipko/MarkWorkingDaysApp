package com.genius.markworkingdaysapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.genius.markworkingdaysapp.data.db.WorkDayDao
import com.genius.markworkingdaysapp.data.db.WorkDayEntity

@Database(
    entities = [WorkDayEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDayDao(): WorkDayDao
}