package com.android.streetworkapp.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import com.android.streetworkapp.model.image.ParkImageLocal
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class ImagesCollectionPopUpTest {
  private lateinit var testImageFile: File
  private lateinit var localParkImages: List<ParkImageLocal>

  @get:Rule val temporaryFolder = TemporaryFolder()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    testImageFile = temporaryFolder.newFile("image1.jpg")
    FileOutputStream(testImageFile).use { outputStream ->
      val dummyData = ByteArray(100) { 0xFF.toByte() } // write dummy data to file
      outputStream.write(dummyData)
    }

    localParkImages =
        listOf(
            ParkImageLocal(
                "dummyHash",
                testImageFile.toUri(),
                "userId",
                "username",
                Pair(0, 0),
                Timestamp.now()))
  }

  // Note: as the image is an AsyncImage, I won't test it
  @Test
  fun `components are displayed correctly`() {

    composeTestRule.setContent { FullScreenImagePopup(localParkImages, mock(), mock(), mock()) {} }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("fullscreenImagePopUp").assertIsDisplayed()
    composeTestRule.onNodeWithTag("fullscreenImagePopUpCloseButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("fullScreenImagePopUpImageBox").assertIsDisplayed()
  }

  @Test
  fun `closing popUp calls dismiss`() {
    val onDismiss = mock(Runnable::class.java)

    composeTestRule.setContent {
      FullScreenImagePopup(localParkImages, mock(), mock(), mock()) { onDismiss.run() }
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("fullscreenImagePopUpCloseButton").performClick()
    verify(onDismiss).run()
  }
}
