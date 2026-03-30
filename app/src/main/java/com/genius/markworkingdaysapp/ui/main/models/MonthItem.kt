package com.genius.markworkingdaysapp.ui.main.models

import java.time.YearMonth

enum class MonthStatus { PAST, CURRENT, FUTURE }

class MonthItem(
    val yearMonth: YearMonth,
    val title: String,
    val status: MonthStatus
)