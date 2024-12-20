package com.android.streetworkapp.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.image.ParkImage
import com.android.streetworkapp.model.image.VOTE_TYPE
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.toFormattedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private object FullScreenImagePopUpSetting {
  val popUpBackgroundColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR
  val fontColor =
      ColorPalette.PRIMARY_TEXT_COLOR // also used for the close pop up icon color (top right)
  val imageInfoFontWeight = FontWeight.Medium
  val dislikeButtonColor = Color(0xffba0d00) // also used for the delete image icon color.
  val undoButtonColor = Color(0xff595858)
}

/** Fullscreen PopUp: it will display all the ParKImageLocal in the list in a HorizontalPager * */
@Composable
fun FullScreenImagePopup(
    images: List<ParkImage>,
    park: Park,
    userViewModel: UserViewModel,
    imageViewModel: ImageViewModel,
    onDismiss: () -> Unit
) {
  val currentUser = userViewModel.currentUser.collectAsState().value
  val currentImages by rememberUpdatedState(images.sortedByDescending { it.rating.getImageScore() })
  // State for the pager to keep track of the current image
  val pagerState = rememberPagerState(pageCount = { currentImages.size })
  val coroutineScope = rememberCoroutineScope()

  val currentImage = currentImages.getOrNull(pagerState.currentPage)

  Dialog(
      onDismissRequest = onDismiss,
      properties =
          DialogProperties(
              usePlatformDefaultWidth = false // Allow the dialog to take full screen width
              )) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier =
                  Modifier.fillMaxSize()
                      .background(FullScreenImagePopUpSetting.popUpBackgroundColor)
                      .let {
                        if (currentImages.isNotEmpty()) it.verticalScroll(rememberScrollState())
                        else it
                      } // only have a vertical scroll if we have images, otherwise can't center the
                      // no image text to the middle of screen,
                      // shouldn't even be needed since the whole thing should fit in the screen but
                      // doesn't hurt to add in the case we have some slight overflow
                      .testTag("fullscreenImagePopUp")) {
                DismissPopUp(onDismiss, this@BoxWithConstraints.maxHeight)
                if (currentImages.isNotEmpty() && currentImage != null) {
                  ImageIndexAndPager(
                      currentImage,
                      currentImages.size,
                      pagerState,
                      this@BoxWithConstraints.maxHeight)

                  UserImageActions(
                      park,
                      currentUser,
                      currentImage,
                      currentImages.size,
                      pagerState,
                      imageViewModel,
                      coroutineScope,
                      this@BoxWithConstraints.maxHeight)
                  ImageInformation(currentImage, this@BoxWithConstraints.maxHeight)
                } else if (currentImages.isEmpty()) {
                  NoImagesDisplay()
                }
              }
        }
      }
}

@Composable
fun DismissPopUp(onDismiss: () -> Unit, maxHeight: Dp) {
  Row(
      modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight * 0.075f),
      horizontalArrangement = Arrangement.End) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(60.dp).testTag("fullscreenImagePopUpCloseButton")) {
              Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close",
                  tint = FullScreenImagePopUpSetting.fontColor)
            }
      }
}

@Composable
fun ImageIndexAndPager(
    currentImage: ParkImage,
    currentImagesSize: Int,
    pagerState: PagerState,
    maxHeight: Dp
) {
  Column(
      modifier = Modifier.heightIn(max = maxHeight * 0.6f),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            " #${pagerState.currentPage + 1}/${currentImagesSize}",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
        )
        // HorizontalPager to swipe between images
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)) {
              ImageItem(imageUri = currentImage.imageUrl)
            }
      }
}

@Composable
fun UserImageActions(
    park: Park,
    currentUser: User?,
    currentImage: ParkImage,
    currentImagesSize: Int,
    pagerState: PagerState,
    imageViewModel: ImageViewModel,
    coroutineScope: CoroutineScope,
    maxHeight: Dp
) {
  Row(
      modifier = Modifier.fillMaxWidth().heightIn(max = maxHeight * 0.1f),
      horizontalArrangement = Arrangement.Center) {
        if (currentUser != null && currentUser.uid != currentImage.userId) {
          if (currentImage.rating.positiveVotesUids.contains(currentUser.uid) ||
              currentImage.rating.negativeVotesUids.contains(currentUser.uid)) {
            Box(modifier = Modifier.padding(horizontal = 15.dp).testTag("retractVoteButton")) {
              // Retract Vote Button
              IconButton(
                  onClick = {
                    imageViewModel.retractImageVote(
                        park.imagesCollectionId,
                        currentImage.imageUrl,
                        currentUser.uid,
                    )
                  },
                  modifier =
                      Modifier.size(60.dp)
                          .clip(CircleShape)
                          .background(FullScreenImagePopUpSetting.undoButtonColor)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Retract Vote",
                        tint = ColorPalette.BUTTON_ICON_COLOR)
                  }
            }
          } else {
            Box(modifier = Modifier.padding(horizontal = 15.dp).testTag("likeImageButton")) {
              // Like Button
              IconButton(
                  onClick = {
                    imageViewModel.imageVote(
                        park.imagesCollectionId,
                        currentImage.imageUrl,
                        currentUser.uid,
                        VOTE_TYPE.POSITIVE)
                  },
                  modifier =
                      Modifier.size(60.dp)
                          .clip(CircleShape)
                          .background(ColorPalette.INTERACTION_COLOR_DARK)) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Like",
                        tint = ColorPalette.BUTTON_ICON_COLOR)
                  }
            }

            Box(modifier = Modifier.padding(horizontal = 15.dp).testTag("dislikeImageButton")) {
              // Dislike Button
              IconButton(
                  onClick = {
                    imageViewModel.imageVote(
                        park.imagesCollectionId,
                        currentImage.imageUrl,
                        currentUser.uid,
                        VOTE_TYPE.NEGATIVE)
                  },
                  modifier =
                      Modifier.size(60.dp)
                          .clip(CircleShape)
                          .background(FullScreenImagePopUpSetting.dislikeButtonColor)) {
                    Icon(
                        imageVector = Icons.Filled.ThumbDown,
                        contentDescription = "Dislike",
                        tint = ColorPalette.BUTTON_ICON_COLOR)
                  }
            }
          }
        } else { // The user who uploaded the picture should only be able to delete
          // it, not vote on it
          Box(modifier = Modifier.padding(horizontal = 15.dp).testTag("deleteImageButton")) {
            // Delete Button
            IconButton(
                onClick = {
                  imageViewModel.deleteImage(
                      park.imagesCollectionId,
                      currentImage.imageUrl,
                      {
                        coroutineScope.launch {
                          if (pagerState.currentPage == currentImagesSize)
                              pagerState.scrollToPage(pagerState.currentPage - 1)
                        }
                      },
                      {})
                },
                modifier =
                    Modifier.size(60.dp)
                        .clip(CircleShape)
                        .background(FullScreenImagePopUpSetting.dislikeButtonColor)) {
                  Icon(
                      imageVector = Icons.Filled.Delete,
                      contentDescription = "Delete button",
                      tint = ColorPalette.BUTTON_ICON_COLOR)
                }
          }
        }
      }
}

@Composable
fun ImageInformation(currentImage: ParkImage, maxHeight: Dp) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(5.dp),
      modifier = Modifier.padding(top = 25.dp).heightIn(max = maxHeight * 0.2f)) {
        Text(
            "Uploaded by ${currentImage.username}",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight)
        Text(
            "The ${currentImage.uploadDate.toFormattedString()}",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight)
        Text(
            "${currentImage.rating.positiveVotes} user(s) liked this picture",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight)
        Text(
            "${currentImage.rating.negativeVotes} user(s) disliked this picture",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight)
      }
}

@Composable
fun ImageItem(imageUri: String) {
  Box(modifier = Modifier.fillMaxSize().testTag("fullScreenImagePopUpImageBox")) {
    AsyncImage(
        modifier = Modifier.align(Alignment.Center),
        model = imageUri,
        contentDescription = "User taken picture.",
        contentScale = ContentScale.Fit,
    )
  }
}

@Composable
fun NoImagesDisplay() {
  Column(
      modifier = Modifier.fillMaxSize().testTag("noImagesDisplay"),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "No image(s) are uploaded for this park.",
            color = FullScreenImagePopUpSetting.fontColor,
            fontWeight = FullScreenImagePopUpSetting.imageInfoFontWeight)
      }
}
