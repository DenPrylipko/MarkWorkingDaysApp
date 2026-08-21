package com.genius.markworkingdaysapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.genius.markworkingdaysapp.model.DayStatus

@Entity(tableName = "work_days")
data class WorkDayEntity(
    @PrimaryKey
    val epochDay: Long,
    val status: DayStatus,

    val bonus: Int?,
    val earned: Int,

    val note: String?
) {
}