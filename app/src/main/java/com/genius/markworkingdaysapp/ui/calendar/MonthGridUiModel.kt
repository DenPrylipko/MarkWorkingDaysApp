package com.genius.markworkingdaysapp.ui.calendar

data class MonthGridUiModel(
    val cells: List<DayCellUiModel>,
) {

    init {
        require(cells.isNotEmpty()) {
         "Month grid cells must not be empty"
        }
    }

    val firstCell: DayCellUiModel
        get() = cells.first()

    val lastCell: DayCellUiModel
        get() = cells.last()
}