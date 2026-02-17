package com.genius.markworkingdaysapp.ui.main.model

import java.time.LocalDate

data class DayCell(
    val date: LocalDate,
    val isInCurrentMonth: Boolean,
    val worked: Boolean = false,
    val bonus: Int? = null,
    val note: String? = null,
    val hasEntry: Boolean = false,
)