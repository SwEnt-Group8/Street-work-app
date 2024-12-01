package com.android.streetworkapp.ui.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.android.streetworkapp.ui.theme.ColorPalette
import java.io.InputStream

@Composable
fun ConfirmImageDialog(
    imageUri: Uri,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    context: Context
) {

    var isImageLoading by remember { mutableStateOf(true) }



  Dialog(onDismissRequest = { onCancel() }) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add this Photo to the Park?",
                    fontSize = 18.sp,
                    color = ColorPalette.PRIMARY_TEXT_COLOR,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("dialogTitle")
                )

               AsyncImage(
                    model = imageUri,
                    contentDescription = "Taken camera picture",
                    modifier = Modifier.fillMaxWidth().testTag("pictureShown"),
                    onSuccess = { isImageLoading = false},
                    onError = {isImageLoading = false}
                )

              if (isImageLoading) {
                  Text("Image is loading...", modifier = Modifier.padding(15.dp), fontWeight = FontWeight.SemiBold)
              }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text =
                        "Disclaimer: Your photo will be linked to your account and visible to anyone using the app.",
                    color = ColorPalette.SECONDARY_TEXT_COLOR,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("disclaimerText")
                    )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()) {
                      Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        // Cancel icon button
                        IconButton(
                            onClick = { onCancel() },
                            modifier =
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.error, shape = CircleShape
                                )
                                .testTag("cancelButton")) {
                              Icon(
                                  imageVector = Icons.Default.Close,
                                  contentDescription = "Cancel",
                                  tint = Color.White)
                            }
                      }

                      Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        // Confirm (upload) icon button
                        IconButton(
                            onClick = { onConfirm() },
                            modifier =
                            Modifier
                                .background(
                                    color = ColorPalette.INTERACTION_COLOR_DARK,
                                    shape = CircleShape
                                )
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
