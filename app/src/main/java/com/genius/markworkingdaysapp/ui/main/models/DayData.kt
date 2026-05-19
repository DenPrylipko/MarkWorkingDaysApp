package com.genius.markworkingdaysapp.ui.main.models

import java.time.LocalDate

data class DayData(
    val date: LocalDate,
    val dayType: DayType,
    val bonus: Int?,
    val shortDayEarned: Int?,
    val note: String?
)