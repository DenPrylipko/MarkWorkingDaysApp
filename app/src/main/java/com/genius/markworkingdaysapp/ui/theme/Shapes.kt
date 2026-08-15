package com.genius.markworkingdaysapp.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

internal object AppRadius {
    val radius8 = 8.dp
    val radius16 = 16.dp
    val radius28 = 28.dp
}

internal val AppShapes = Shapes(
    small = RoundedCornerShape(AppRadius.radius8),
    medium = RoundedCornerShape(AppRadius.radius16),
    large = RoundedCornerShape(AppRadius.radius28),
)