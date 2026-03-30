package com.genius.markworkingdaysapp.ui.main.models

import java.time.LocalDate

data class DayCell(
    val hasEntry: Boolean = false,
    val isInCurrentMonth: Boolean,
    val date: LocalDate,

    val dayType: DayType? = null,

    val bonus: Int? = null,
    val earned: Int? = null,
    val note: String? = null,
)

enum class DayType {
    FULL,
    SHORT,
    NOT_WORKED
}