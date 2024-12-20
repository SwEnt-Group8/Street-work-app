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

  val showConfirmationDialog = remember { mutableStateOf(false) }
  var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

  // Temporary file for storing the captured image
  val tempImageFile by remember {
    mutableStateOf(
        File(context.cacheDir, "temp_image_for_park_upload.jpg").apply {
          if (exists() && !delete()) {
            Log.d(AddImageButtonParams.DEBUG_PREFIX, "Failed to delete cached photo file.")
          }
        })
  }

  capturedImageUri =
      FileProvider.getUriForFile(context, "${context.packageName}.provider", tempImageFile)

  val cameraLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.TakePicture(),
          onResult = { success ->
            if (success) {
              showConfirmationDialog.value = true
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
                Modifier.size(38.dp)
                    .background(color = ColorPalette.INTERACTION_COLOR_DARK, shape = CircleShape)
                    .padding(2.dp)) {
              Icon(
                  painter = painterResource(id = R.drawable.add_a_photo_24px),
                  contentDescription = "Add Image",
                  tint = ColorPalette.BUTTON_ICON_COLOR,
                  modifier = Modifier.align(Alignment.Center).fillMaxSize(0.75f))
            }
      }

  capturedImageUri?.let { uri ->
    currentPark?.let { park ->
      currentUser?.let { user ->
        ConfirmImageDialogWrapper(
            context, tempImageFile, uri, showConfirmationDialog, imageViewModel, park, user)
      }
    }
  }
}
