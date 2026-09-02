package com.genius.markworkingdaysapp.ui.common.dialog

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

internal object AppDialogDefaults {
    const val DIM_AMOUNT = 0.55f
}

@Composable
fun AppDialog(
    onDismissRequest: () -> Unit,
    dimAmount: Float = AppDialogDefaults.DIM_AMOUNT,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
    ),
    content: @Composable () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        val view = LocalView.current

        SideEffect {
            (view.parent as? DialogWindowProvider)
                ?.window
                ?.apply {
                    addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    setDimAmount(dimAmount.coerceIn(0f, 1f))
                }
        }

        content()

    }

}