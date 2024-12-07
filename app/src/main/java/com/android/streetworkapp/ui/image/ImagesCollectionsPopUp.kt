package com.android.streetworkapp.ui.image

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.android.streetworkapp.model.image.ParkImageLocal

@Composable
fun FullScreenImagePopup(images: List<ParkImageLocal>, onDismiss: () -> Unit) {
  // State for the pager to keep track of the current image
  val imageUris by remember { mutableStateOf(images.map { it.image }) }
  val pagerState = rememberPagerState(pageCount = { imageUris.size })

  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              usePlatformDefaultWidth = false // Allow the dialog to take full screen width
              )) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.80f))) {
          // HorizontalPager to swipe between images
          HorizontalPager(
              state = pagerState,
              modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f).align(Alignment.Center)) { page
                ->
                ImageItem(imageUri = imageUris[page])
              }

          IconButton(onClick = onDismiss, modifier = Modifier.align(Alignment.TopEnd).size(60.dp)) {
            Icon(
                imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }
      }
}

@Composable
fun ImageItem(imageUri: Uri) {
  Box(modifier = Modifier.fillMaxSize()) {
    AsyncImage(
        modifier = Modifier.align(Alignment.Center),
        model = imageUri,
        contentDescription = "User taken picture.",
        contentScale = ContentScale.Fit,
    )
  }
}
