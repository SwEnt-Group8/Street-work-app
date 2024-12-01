package com.android.streetworkapp.ui.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun DisplayRawBase64ToImage(base64ImageData: String) {
    val imageData by remember { mutableStateOf(Base64.decode(base64ImageData, 0, base64ImageData.length)) }
    val bitmap by remember {  mutableStateOf(BitmapFactory.decodeByteArray(imageData, 0, imageData.size)) }

    bitmap?.let {
        Column(modifier = Modifier.padding(top=70.dp)) {
            CaptureAndEncodeImageScreenWithPermission()
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "decoded image" //TODO: change the content desc
            )
        }
    }
}



@Composable
fun CaptureAndEncodeImageScreenWithPermission() {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    if (hasCameraPermission) {
        CaptureAndEncodeImageScreen(context)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission is required to take a picture.")
            Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun CaptureAndEncodeImageScreen(context: Context) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64String by remember { mutableStateOf("") }

    // Temporary file for storing the captured image
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg").apply {
        createNewFile()
        deleteOnExit()
    }

    // Generate a content:// URI using FileProvider
    val tempUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                capturedImageUri = tempUri
                capturedImageUri?.let { uri ->
                    base64String = encodeImageToBase64(context, uri)
                    //TODO: save the image to db here
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = { launcher.launch(tempUri) }) {
            Text(text = "Take Picture")
        }

        capturedImageUri?.let {
            Text(text = "Image Captured:")
            Image(
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it).asImageBitmap(),
                contentDescription = "Captured Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        if (base64String.isNotEmpty()) {
            Text(text = "Base64 Encoded Image:")
            Text(
                text = base64String,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
fun encodeImageToBase64(context: Context, imageUri: Uri): String {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri) //TODO: check depreciated alternative
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream) //TODO: check if this does anything
    val byteArray = outputStream.toByteArray()
    return Base64.encode(byteArray, 0, byteArray.size)
}

@Composable
fun takePictureOrAskPermissionIfNotYetGranted(context: Context) {
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64String by remember { mutableStateOf("") }

    // Temporary file for storing the captured image
    val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg").apply {
        createNewFile()
        deleteOnExit()
    }

    // Generate a content:// URI using FileProvider
    val tempUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                capturedImageUri = tempUri
                capturedImageUri?.let { uri ->
                    base64String = encodeImageToBase64(context, uri)
                    //TODO: save the image to db here
                }
            }
        }
    )
}
