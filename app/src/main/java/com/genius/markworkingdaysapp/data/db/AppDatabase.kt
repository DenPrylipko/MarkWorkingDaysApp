package com.genius.markworkingdaysapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.genius.markworkingdaysapp.data.db.dao.WorkDayDao
import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity

@Database(
    entities = [WorkDayEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workDayDao(): WorkDayDao
}