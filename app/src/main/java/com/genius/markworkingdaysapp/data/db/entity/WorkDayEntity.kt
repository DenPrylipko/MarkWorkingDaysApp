package com.genius.markworkingdaysapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_days")
data class WorkDayEntity(
    @PrimaryKey val epochDay: Long,
    val worked: Boolean,
    val bonus: Int?,
    val note: String?
) {
}