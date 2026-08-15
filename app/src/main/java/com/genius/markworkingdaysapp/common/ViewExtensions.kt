package com.genius.markworkingdaysapp.common

import android.view.View

fun View.hapticClick(type: Int) {
    performHapticFeedback(type)
}