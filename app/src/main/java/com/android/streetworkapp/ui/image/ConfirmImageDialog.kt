package com.android.streetworkapp.ui.image

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.streetworkapp.ui.theme.ColorPalette

@Composable
fun ConfirmImageDialog(
    imageUri: Uri,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    key: String = ""
) {
  var isImageLoading by remember { mutableStateOf(true) }

  Dialog(onDismissRequest = { onCancel() }) {
    BoxWithConstraints {
      Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          modifier =
              Modifier.fillMaxWidth()
                  .heightIn(
                      max =
                          this@BoxWithConstraints.maxHeight *
                              0.8f) // Take at most 80% of the height
          ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)) {
                  Text(
                      text = "Add this Photo to the Park?",
                      fontSize = 18.sp,
                      color = ColorPalette.PRIMARY_TEXT_COLOR,
                      fontWeight = FontWeight.SemiBold,
                      modifier = Modifier.padding(bottom = 16.dp).testTag("dialogTitle"))

                  Box(
                      modifier =
                          Modifier.heightIn(max = this@BoxWithConstraints.maxHeight * 0.5f)) {
                        AsyncImage(
                            model =
                                ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .memoryCachePolicy(
                                        CachePolicy
                                            .DISABLED) // since we reuse the same file (thus same
                                    // URI), if we keep things cached it will
                                    // keep showing the first image. Since it's
                                    // only for one image it's not a big perf hit
                                    .build(),
                            contentDescription = "Taken camera picture",
                            modifier = Modifier.testTag("pictureShown"),
                            contentScale = ContentScale.Fit,
                            onSuccess = { isImageLoading = false },
                            onError = {
                              isImageLoading = false
                            } // Note: should prob setup and error message but whatever
                            )
                        if (isImageLoading) {
                          Text(
                              "Image is loading...",
                              modifier = Modifier.padding(15.dp),
                              fontWeight = FontWeight.SemiBold)
                        }
                      }

                  Spacer(modifier = Modifier.height(5.dp))

                  Text(
                      text =
                          "Disclaimer: Your photo will be linked to your account and visible to anyone using the app.",
                      color = ColorPalette.SECONDARY_TEXT_COLOR,
                      fontSize = 12.sp,
                      textAlign = TextAlign.Center,
                      modifier = Modifier.padding(horizontal = 25.dp).testTag("disclaimerText"))

                  Spacer(modifier = Modifier.height(16.dp))

                  Row(
                      horizontalArrangement = Arrangement.Center,
                      modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                          IconButton(
                              onClick = { onCancel() },
                              modifier =
                                  Modifier.background(
                                          color = MaterialTheme.colorScheme.error,
                                          shape = CircleShape)
                                      .size(45.dp)
                                      .testTag("cancelButton")) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White)
                              }
                        }

                        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                          IconButton(
                              onClick = { onConfirm() },
                              modifier =
                                  Modifier.background(
                                          color = ColorPalette.INTERACTION_COLOR_DARK,
                                          shape = CircleShape)
                                      .size(45.dp)
                                      .testTag("uploadButton")) {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = "Upload",
                                    tint = Color.White)
                              }
                        }
                      }
                }
          }
    }
  }
}
