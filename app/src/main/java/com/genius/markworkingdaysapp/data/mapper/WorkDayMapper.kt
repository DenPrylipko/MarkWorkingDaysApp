package com.genius.markworkingdaysapp.data.mapper

import com.genius.markworkingdaysapp.data.db.entity.WorkDayEntity
import com.genius.markworkingdaysapp.model.WorkDay
import java.time.LocalDate

internal fun WorkDayEntity.toWorkDay(): WorkDay =
    WorkDay(
        date = LocalDate.ofEpochDay(epochDay),
        status = status,
        bonus = bonus,
        earned = earned,
        note = note,
    )

internal fun WorkDay.toEntity(): WorkDayEntity =
    WorkDayEntity(
        epochDay = date.toEpochDay(),
        status = status,
        bonus = bonus,
        earned = earned,
        note = note,
    )