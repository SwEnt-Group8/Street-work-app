package com.android.streetworkapp.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter


@Composable
fun FullScreenImagePopup(imageUrls: List<String>, onDismiss: () -> Unit) {
    // State for the pager to keep track of the current image
    val pagerState = rememberPagerState(pageCount = { imageUrls.size })

    Dialog(onDismissRequest = onDismiss,     properties = DialogProperties(
        usePlatformDefaultWidth = false
    ),) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)) // Darker background with opacity
        ) {
            // HorizontalPager to swipe between images
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Display each image individually
                ImageItem(imageUrl = imageUrls[page])
            }


            // Close button at the top-right corner with better visibility
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(50.dp)  // Make the button bigger for visibility
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White // Make the button icon white for better visibility
                )
            }
        }
    }
}

@Composable
fun ImageItem(imageUrl: String) {
    val painter: Painter = rememberAsyncImagePainter(imageUrl)

    Box(
        modifier = Modifier
            .fillMaxSize()  // Take the full screen for the image
            .padding(4.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = null,  // Provide a description if necessary
            modifier = Modifier.fillMaxSize()
        )
    }
}