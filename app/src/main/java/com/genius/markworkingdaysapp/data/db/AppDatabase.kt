package com.genius.markworkingdaysapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.genius.markworkingdaysapp.data.db.dao.MonthRateDao
import com.genius.markworkingdaysapp.data.db.dao.WorkDayDao
import com.genius.markworkingdaysapp.data.db.entity.MonthRateEntity
import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity

@Database(
    entities = [
        WorkDayEntity::class,
        MonthRateEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workDayDao(): WorkDayDao

    abstract fun monthRateDao(): MonthRateDao
}