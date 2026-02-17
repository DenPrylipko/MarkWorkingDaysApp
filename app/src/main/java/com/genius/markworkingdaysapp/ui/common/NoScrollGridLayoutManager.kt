package com.genius.markworkingdaysapp.ui.common

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class NoScrollGridLayoutManager(
    context: Context,
    spanCount: Int
) : GridLayoutManager(context, spanCount) {

    override fun canScrollVertically(): Boolean = false
    override fun canScrollHorizontally(): Boolean = false

}