package com.android.streetworkapp.ui.image

import android.graphics.BitmapFactory
import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun DisplayRawBase64ToImage(base64ImageData: String) {
    val imageData = Base64.decode(base64ImageData, 0, base64ImageData.length)
    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

    bitmap?.let {
        Box(modifier = Modifier.padding(top=70.dp)) {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "decoded image" //TODO: change the content desc
            )
        }
    }

}