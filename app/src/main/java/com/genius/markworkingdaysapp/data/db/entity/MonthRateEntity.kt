package com.genius.markworkingdaysapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "month_rates")
data class MonthRateEntity(
    @PrimaryKey
    val monthStartEpochDay: Long,

    val dailyRate: Int
)