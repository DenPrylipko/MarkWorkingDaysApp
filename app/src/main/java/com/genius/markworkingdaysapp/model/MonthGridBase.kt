package com.genius.markworkingdaysapp.model

import java.time.LocalDate

data class MonthGridBase(
    val cells: List<DayCell>,
    val start: LocalDate,
    val end: LocalDate,
)
