package com.genius.markworkingdaysapp.ui.common.yearmonthdialog

import com.genius.markworkingdaysapp.common.getMonthTitle
import com.genius.markworkingdaysapp.model.MonthStatus
import java.time.YearMonth

data class MonthItem(
    val yearMonth: YearMonth,
    val status: MonthStatus
) {
    val title: String
        get() = getMonthTitle(yearMonth)
}