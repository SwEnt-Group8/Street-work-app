package com.android.streetworkapp.ui.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.sample.R
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.ui.theme.ColorPalette
import java.io.File

//TODO: setup description for this composable
@Composable
fun AddImageButton(currentPark : Park?, context: Context) {

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Temporary file for storing the captured image
    //TODO: check to add delete on exit
    val tempFile = File(context.cacheDir, "temp_image_for_park_upload.jpg")

    capturedImageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        tempFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                showConfirmationDialog = true
                //onImageCaptured(tempUri) // Pass the URI to the parent composable for further processing
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
            if (isGranted) {
                cameraLauncher.launch(capturedImageUri)
            }
        }
    )

    IconButton(
        onClick = {
            if (hasCameraPermission)
                cameraLauncher.launch(capturedImageUri)
            else
                permissionLauncher.launch(Manifest.permission.CAMERA)
        },
        modifier = Modifier.testTag("addImageIconButton")
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = ColorPalette.INTERACTION_COLOR_DARK,
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_a_photo_24px), // Replace with your "add image" icon
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).fillMaxSize(0.75f)
            )
        }
    }

    capturedImageUri?.let {
        if (showConfirmationDialog) {
            ConfirmImageDialog(
                imageUri = it,
                //TODO: delete image after use
                onConfirm = {
                    //onImageUploaded(capturedImageUri!!)
                    showConfirmationDialog = false
                },
                onCancel = {
                    showConfirmationDialog = false
                },
                context
            )
        }
    }
}