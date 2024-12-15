package com.android.streetworkapp.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.model.image.ImageRepository
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
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

  private var currentUser =
      User(
          uid = "12345",
          username = "john_doe",
          email = "john.doe@example.com",
          score = 1200,
          friends = listOf("67890", "23456", "34567"),
          picture = "https://example.com/profile_pic.jpg",
          parks = listOf("park1", "park2", "park3"))

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
}
