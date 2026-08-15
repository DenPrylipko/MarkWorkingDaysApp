package com.genius.markworkingdaysapp.model

import java.time.LocalDate

data class DayData(
    val date: LocalDate,
    val dayType: DayType,
    val bonus: Int?,
    val shortDayEarned: Int?,
    val note: String?
)