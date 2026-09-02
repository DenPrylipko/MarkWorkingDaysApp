package com.genius.markworkingdaysapp.ui.calendar.model

import com.genius.markworkingdaysapp.model.WorkDay
import java.time.LocalDate

data class DayCellUiModel(
    val date: LocalDate,
    val workDay: WorkDay?,
) {
    val hasEntry: Boolean
        get() = workDay != null

}