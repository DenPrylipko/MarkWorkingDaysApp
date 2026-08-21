package com.genius.markworkingdaysapp.model

import java.time.LocalDate

data class WorkDay(
    val date: LocalDate,
    val status: DayStatus,
    val bonus: Int? = null,
    val earned: Int = 0,
    val note: String? = null
) {
    fun withRecalculatedEarned(dailyRate: Int): WorkDay {
        return when(status) {
            DayStatus.FULL_DAY -> copy(
                earned = dailyRate + (bonus ?: 0)
            )

            DayStatus.SHORT_DAY -> this

            DayStatus.NOT_WORKED -> copy(
                earned = 0
            )
        }
    }
}