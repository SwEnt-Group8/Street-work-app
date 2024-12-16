package com.android.streetworkapp.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.model.image.ImageRating
import com.android.streetworkapp.model.image.ImageRepository
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.image.ParkImage
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class FullScreenImagePopupTest {

  @Mock private lateinit var userRepository: UserRepository
  @Mock private lateinit var imageRepository: ImageRepository

  @InjectMocks private lateinit var userViewModel: UserViewModel
  @InjectMocks private lateinit var imageViewModel: ImageViewModel

  private var baseUser =
      User(
          uid = "12345",
          username = "john_doe",
          email = "john.doe@example.com",
          score = 1200,
          friends = listOf("67890", "23456", "34567"),
          picture = "https://example.com/profile_pic.jpg",
          parks = listOf("park1", "park2", "park3"))

  private var baseImage =
      ParkImage(
          imageUrl = "https://dummyimageulr.com",
          userId = "ownerUsedId",
          username = "ownerUsername",
          rating =
              ImageRating(
                  positiveVotes = 1,
                  positiveVotesUids = listOf("positiveVoterUid"),
                  negativeVotes = 0,
                  negativeVotesUids = emptyList()),
          uploadDate = Timestamp.now())

  private var park = Park(pid = "parkId123")

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `NoImagesDisplay is displayed on empty image list`() {
    composeTestRule.setContent {
      FullScreenImagePopup(emptyList(), park, userViewModel, imageViewModel) {}
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("noImagesDisplay").assertIsDisplayed()
  }

  @Test
  fun `retractVote button is shown if user is in image voting list`() {
    val userInVotingList = baseUser.copy(uid = baseImage.rating.positiveVotesUids[0])
    userViewModel.setCurrentUser(userInVotingList)

    composeTestRule.setContent {
      FullScreenImagePopup(listOf(baseImage), park, userViewModel, imageViewModel) {}
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("retractVoteButton").assertIsDisplayed()
  }

  @Test
  fun `deleteImage button is shown if user is owner of image`() {
    val userOwnerOfImage = baseUser.copy(uid = baseImage.userId)
    userViewModel.setCurrentUser(userOwnerOfImage)

    composeTestRule.setContent {
      FullScreenImagePopup(listOf(baseImage), park, userViewModel, imageViewModel) {}
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("deleteImageButton").assertIsDisplayed()
  }

  @Test
  fun `voteImage buttons are shown if user is neither owner of image nor in image voting list`() {
    userViewModel.setCurrentUser(baseUser)

    composeTestRule.setContent {
      FullScreenImagePopup(listOf(baseImage), park, userViewModel, imageViewModel) {}
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("likeImageButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dislikeImageButton").assertIsDisplayed()
  }
}
