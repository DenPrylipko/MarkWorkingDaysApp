package com.genius.markworkingdaysapp.ui.calendar.share

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

fun shareText(
    context: Context,
    text: String,
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    context.startActivity(
        Intent.createChooser(intent, null)
    )
}

suspend fun shareImage(
    context: Context,
    image: ImageBitmap,
    text: String? = null,
) {
    val imageUri = withContext(Dispatchers.IO) {
        saveImageForSharing(
            context = context,
            image = image,
        )
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"

        putExtra(Intent.EXTRA_STREAM, imageUri)

        text?.let {
            putExtra(Intent.EXTRA_TEXT, it)
        }

        clipData = ClipData.newUri(
            context.contentResolver,
            "Calendar image",
            imageUri,
        )

        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(
        Intent.createChooser(intent, null)
    )

}

private fun saveImageForSharing(
    context: Context,
    image: ImageBitmap
): Uri {
    val directory = File(
        context.cacheDir,
        "shared",
    ).apply {
        mkdirs()
    }

    val file = File(
        directory,
        "calendar.png",
    )

    FileOutputStream(file).use { outputStream ->
        image.asAndroidBitmap().compress(
            Bitmap.CompressFormat.PNG,
            100,
            outputStream,
        )
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

}
