package com.genius.markworkingdaysapp.model

import java.time.YearMonth

enum class MonthStatus { CURRENT, PAST_NOT_WORKED, PAST_WORKED, FUTURE }

class MonthItem(
    val yearMonth: YearMonth,
    val title: String,
    val status: MonthStatus
)