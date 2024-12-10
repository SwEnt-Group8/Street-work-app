package com.android.streetworkapp.ui.image

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.android.streetworkapp.model.image.ParkImageLocal
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.toFormattedString

private object FullScreenImagePopUpSetting {
    val popUpBackgroundColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR
    val fontColor = ColorPalette.PRIMARY_TEXT_COLOR //also used for the close pop up icon color (top right)
    val imageInfoFontWeight = FontWeight.Medium
    val dislikeButtonColor = Color(0xffba0d00) //also used for the delete image icon color.
}

/** Fullscreen PopUp: it will display all the ParKImageLocal in the list in a HorizontalPager **/
@SuppressLint("AutoboxingStateCreation")
@Composable
fun FullScreenImagePopup(images: List<ParkImageLocal>, userViewModel: UserViewModel, onDismiss: () -> Unit) {

    val currentUser = userViewModel.currentUser.collectAsState().value

    val imagesSize by remember { mutableIntStateOf(images.size) }

  // State for the pager to keep track of the current image
  val pagerState = rememberPagerState(pageCount = { imagesSize })

    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false // Allow the dialog to take full screen width
                )) {

          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier =
                  Modifier.fillMaxSize()
                      .background(FullScreenImagePopUpSetting.popUpBackgroundColor)
                      .let {
                          if (imagesSize > 0) it.verticalScroll(rememberScrollState())
                          else it
                      } //only have a vertical scroll if we have images
                      .testTag("fullscreenImagePopUp")) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                  IconButton(
                      onClick = onDismiss,
                      modifier =
                      Modifier.size(60.dp)
                          .testTag("fullscreenImagePopUpCloseButton")
                  ) {
                      Icon(
                          imageVector = Icons.Default.Close,
                          contentDescription = "Close",
                          tint = FullScreenImagePopUpSetting.fontColor
                      )
                  }
              }
              if (imagesSize > 0) {
                  Text(
                      " #${pagerState.currentPage + 1}/${imagesSize}",
                      color = FullScreenImagePopUpSetting.fontColor,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 18.sp
                  )
                  // HorizontalPager to swipe between images
                  HorizontalPager(
                      state = pagerState,
                      modifier =
                      Modifier.fillMaxWidth().fillMaxHeight(0.7f).padding(vertical = 20.dp)
                  ) { page ->
                      ImageItem(imageUri = images[page].image)
                  }
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.Center
                  ) {
                      if (currentUser?.uid != images[pagerState.currentPage].userId) {
                          Box(modifier = Modifier.padding(horizontal = 15.dp)) {
                              // Like Button
                              IconButton(
                                  onClick = { /*onLike(imageUris[pagerState.currentPage])*/ },
                                  modifier =
                                  Modifier.size(60.dp)
                                      .clip(CircleShape)
                                      .background(ColorPalette.INTERACTION_COLOR_DARK)
                              ) {
                                  Icon(
                                      imageVector = Icons.Filled.ThumbUp,
                                      contentDescription = "Like",
                                      tint = Color.White
                                  )
                              }
                          }

                          Box(modifier = Modifier.padding(horizontal = 15.dp)) {
                              // Dislike Button
                              IconButton(
                                  onClick = { /*onDislike(imageUris[pagerState.currentPage])*/ },
                                  modifier =
                                  Modifier.size(60.dp).clip(CircleShape)
                                      .background(FullScreenImagePopUpSetting.dislikeButtonColor)
                              ) {
                                  Icon(
                                      imageVector = Icons.Filled.ThumbDown,
                                      contentDescription = "Dislike",
                                      tint = Color.White
                                  )
                              }
                          }
                      } else { //The user who uploaded the picture should only be able to delete it, not vote on it
                          Box(modifier = Modifier.padding(horizontal = 15.dp)) {
                              // Dislike Button
                              IconButton(
                                  onClick = { /*onDelete(imageUris[pagerState.currentPage])*/ },
                                  modifier =
                                  Modifier.size(60.dp).clip(CircleShape)
                                      .background(FullScreenImagePopUpSetting.dislikeButtonColor)
                              ) {
                                  Icon(
                                      imageVector = Icons.Filled.Delete,
                                      contentDescription = "Dislike",
                                      tint = Color.White
                                  )
                              }
                          }
                      }
                  }

                  Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.spacedBy(5.dp),
                      modifier = Modifier.padding(vertical = 25.dp)
                  ) {
                      Text(
                          "Uploaded by ${images[pagerState.currentPage].username}",
                          color = FullScreenImagePopUpSetting.fontColor,
                          fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight
                      )
                      Text(
                          "The ${images[pagerState.currentPage].uploadDate.toFormattedString()}",
                          color = FullScreenImagePopUpSetting.fontColor,
                          fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight
                      )
                      Text(
                          "${images[pagerState.currentPage].rating.first} user(s) liked this picture",
                          color = FullScreenImagePopUpSetting.fontColor,
                          fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight
                      )
                      Text(
                          "${images[pagerState.currentPage].rating.second} user(s) disliked this picture",
                          color = FullScreenImagePopUpSetting.fontColor,
                          fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight
                      )
                  }
              } else {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      verticalArrangement = Arrangement.Center,
                      horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                      Text(
                          text = "No image(s) are uploaded for this park.",
                          color = FullScreenImagePopUpSetting.fontColor,
                          fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight
                      )
                  }
              }
              }
        }
}

@Composable
fun ImageItem(imageUri: Uri) {
  Box(modifier = Modifier.fillMaxSize().testTag("fullScreenImagePopUpImageBox")) {
    AsyncImage(
        modifier = Modifier.align(Alignment.Center),
        model = imageUri,
        contentDescription = "User taken picture.",
        contentScale = ContentScale.Fit,
    )
  }
}
