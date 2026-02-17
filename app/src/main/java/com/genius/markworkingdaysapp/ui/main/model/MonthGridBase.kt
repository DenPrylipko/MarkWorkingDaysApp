package com.genius.markworkingdaysapp.ui.main.model

import java.time.LocalDate

data class MonthGridBase(
    val cells: List<DayCell>,
    val start: LocalDate,
    val end: LocalDate,
)
