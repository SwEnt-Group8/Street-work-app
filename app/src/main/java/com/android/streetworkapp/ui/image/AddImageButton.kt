package com.android.streetworkapp.ui.image

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.sample.R
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.ui.theme.ColorPalette
import java.io.File

object AddImageButtonParams {
  const val DEBUG_PREFIX = "AddImageButton:"
}

/**
 * IconButton that on onclick will request permission (if not already done) and open the camera to
 * take a picture. After a picture is taken, a [ConfirmImageDialog] will be shown to prompt the user
 * to cancel or upload the file
 *
 * @param currentPark The info of the park the image will be linked to
 */
@Composable
fun AddImageButton(imageViewModel: ImageViewModel, currentPark: Park?, currentUser: User?) {

  val context = LocalContext.current

  var hasCameraPermission by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED)
  }

  var showConfirmationDialog by remember { mutableStateOf(false) }
  var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

  // Temporary file for storing the captured image
  val tempFile by remember {
    mutableStateOf(
        File(context.cacheDir, "temp_image_for_park_upload.jpg").apply {
          if (exists() && !delete()) {
            Log.d(AddImageButtonParams.DEBUG_PREFIX, "Failed to delete cached photo file.")
          }
        })
  }

  capturedImageUri =
      FileProvider.getUriForFile(context, "${context.packageName}.provider", tempFile)

  val cameraLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.TakePicture(),
          onResult = { success ->
            if (success) {
              showConfirmationDialog = true
            }
          })

  val permissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted ->
            hasCameraPermission = isGranted
            if (isGranted) {
              cameraLauncher.launch(capturedImageUri)
            }
          })

  IconButton(
      onClick = {
        if (hasCameraPermission) cameraLauncher.launch(capturedImageUri)
        else permissionLauncher.launch(Manifest.permission.CAMERA)
      },
      modifier = Modifier.testTag("addImageIconButton")) {
        Box(
            modifier =
                Modifier.size(36.dp)
                    .background(color = ColorPalette.INTERACTION_COLOR_DARK, shape = CircleShape)
                    .padding(4.dp)) {
              Icon(
                  painter = painterResource(id = R.drawable.add_a_photo_24px),
                  contentDescription = "Add Image",
                  tint = Color.White,
                  modifier = Modifier.align(Alignment.Center).fillMaxSize(0.75f))
            }
      }

  capturedImageUri?.let { uri ->
    currentPark?.let { park ->
      currentUser?.let { user ->
        if (showConfirmationDialog) {
          ConfirmImageDialog(
              imageUri = uri,
              onConfirm = {
                imageViewModel.uploadImage(
                    context,
                    uri,
                    park.pid,
                    user.uid,
                    { onImageUploadSuccess(tempFile) },
                    { onImageUploadFailure() })
                showConfirmationDialog = false
                // tempFile.delete() // IMPORTANT: only delete the file when the viewmodel is done
                // with
                // it,
                // will cause race conditions otherwise
              },
              onCancel = {
                showConfirmationDialog = false
                if (!tempFile.delete())
                    Log.d(AddImageButtonParams.DEBUG_PREFIX, "Failed to delete cached photo file.")
              })
        }
      }
    }
  }
}

// TODO: make UI responsive depending on success/failure
private fun onImageUploadSuccess(file: File) {
  if (!file.delete())
      Log.d(AddImageButtonParams.DEBUG_PREFIX, "Failed to delete cached photo file.")
}

private fun onImageUploadFailure() {}
