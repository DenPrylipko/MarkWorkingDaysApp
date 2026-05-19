package com.genius.markworkingdaysapp.ui.main.models

import java.time.LocalDate
import java.time.YearMonth

enum class MonthStatus { CURRENT, PAST_NOT_WORKED, PAST_WORKED, FUTURE }

class MonthItem(
    val yearMonth: YearMonth,
    val title: String,
    val status: MonthStatus
)