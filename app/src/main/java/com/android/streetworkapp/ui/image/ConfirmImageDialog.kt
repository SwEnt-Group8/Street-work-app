package com.android.streetworkapp.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.android.sample.R
import com.android.streetworkapp.ui.theme.ColorPalette
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.io.File
import java.io.InputStream

@Composable
fun ConfirmImageDialog(
    imageUri: Uri,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    context: Context
) {
    //the inputStream and imageBitmap is ugly but I couldn't for the life of me make AsyncImage load directly from the image uri as it should
    val inputStream  by remember { mutableStateOf<InputStream?>(context.contentResolver.openInputStream(imageUri)) }
    val imageBitmap by remember { mutableStateOf<Bitmap?>(inputStream?.let {BitmapFactory.decodeStream(inputStream) })}

    Dialog(onDismissRequest = { onCancel() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Confirm Image",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                    imageBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Taken camera picture",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel button
                        Button(
                            onClick = { onCancel() },
                            colors = ColorPalette.BUTTON_COLOR
                        ) {
                            Text(text = "Cancel", color = Color.Black)
                        }
                        // Confirm button
                        Button(
                            onClick = { onConfirm() }
                        ) {
                            Text(text = "Upload")
                        }
                    }
                }
            }
        }
    }