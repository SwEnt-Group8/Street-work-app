package com.android.streetworkapp.ui.image

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
import com.android.sample.R
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.image.ParkImage
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.theme.ColorPalette

/** Button to access the images collection popup. */
@Composable
fun ImagesCollectionButton(
    imageViewModel: ImageViewModel,
    userViewModel:
        UserViewModel, // note: not used for now, will be used for the rating etc... later on.
    park: Park?
) {

  val context = LocalContext.current

  var showImagesCollection by remember { mutableStateOf(false) }
  var parkImages by remember {
    mutableStateOf(emptyList<ParkImage>())
  } // this will store the park images

  park?.let {
    if (showImagesCollection)
        FullScreenImagePopup(parkImages, it, userViewModel, imageViewModel) {
          showImagesCollection = false
        }
  }

  IconButton(
      onClick = {
        park?.let { park ->
          // fetch the images
          imageViewModel.retrieveImages(context, park) {
            parkImages = it
            showImagesCollection = true
          }

          if (park.imagesCollectionId
              .isNotEmpty()) { // register a collection listener if the park has an image collection
            // setup
            imageViewModel.registerCollectionListener(park.imagesCollectionId) {
              imageViewModel.retrieveImages(context, park) { parkImages = it }
            }
          }
        }
      },
      modifier = Modifier.testTag("ImagesCollectionButton")) {
        Box(
            modifier =
                Modifier.size(38.dp)
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
