package com.genius.markworkingdaysapp.ui.main.models

import java.time.LocalDate

data class MonthGridBase(
    val cells: List<DayCell>,
    val start: LocalDate,
    val end: LocalDate,
)
