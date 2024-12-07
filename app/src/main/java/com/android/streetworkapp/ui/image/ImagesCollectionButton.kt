package com.android.streetworkapp.ui.image

import android.Manifest
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
import com.android.sample.R
import com.android.streetworkapp.ui.theme.ColorPalette

@Composable
fun ImagesCollectionButton() {

    var showImagesCollection by remember { mutableStateOf(false) }

    val sampleImageUrls = listOf(
        "https://www.shutterstock.com/image-vector/kindergarten-kids-playground-city-park-260nw-1120558709.jpg",
        "https://images.unsplash.com/photo-1519331379826-f10be5486c6f?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTh8fHBhcmt8ZW58MHx8MHx8fDA%3D",
        "https://as2.ftcdn.net/v2/jpg/00/76/25/75/1000_F_76257590_OMqEbhnSnz30cLj6xAG511xSZrJabcsq.jpg"
    )

    if (showImagesCollection)
        FullScreenImagePopup(sampleImageUrls) { showImagesCollection = false }

    IconButton(
        onClick = {
            showImagesCollection = true
        },
        modifier = Modifier.testTag("ImagesCollectionButton")) {
        Box(
            modifier =
            Modifier.size(36.dp)
                .background(color = ColorPalette.INTERACTION_COLOR_DARK, shape = CircleShape)
                .padding(2.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.photo_library_24px),
                contentDescription = "Add Image",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).fillMaxSize(0.75f))
        }
    }
}