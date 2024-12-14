package com.android.streetworkapp.ui.image

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.user.User
import java.io.File
import java.io.FileOutputStream
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ConfirmImageDialogTest {
  private lateinit var testImageFile: File

  @Mock private lateinit var imageViewModel: ImageViewModel

  @get:Rule val temporaryFolder = TemporaryFolder()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    testImageFile = temporaryFolder.newFile("test_confirm_image_dialog.jpg")
    FileOutputStream(testImageFile).use { outputStream ->
      val dummyData = ByteArray(100) { 0xFF.toByte() } // write dummy data to file
      outputStream.write(dummyData)
    }
  }

  // Note: as the image is an AsyncImage, I won't test it
  @Test
  fun `components are displayed correctly`() {
    composeTestRule.setContent {
      ConfirmImageDialog(imageUri = testImageFile.toUri(), onConfirm = {}, onCancel = {})
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ConfirmImageDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("disclaimerText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadButton").assertIsDisplayed()
  }

  @Test
  fun `cancel and upload buttons call correct functions`() {
    val onConfirm = mock(Runnable::class.java)
    val onCancel = mock(Runnable::class.java)

    composeTestRule.setContent {
      ConfirmImageDialog(
          imageUri = testImageFile.toUri(),
          onConfirm = { onConfirm.run() },
          onCancel = { onCancel.run() })
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("cancelButton").performClick()
    verify(onCancel).run()

    composeTestRule.onNodeWithTag("uploadButton").performClick()
    verify(onConfirm).run()
  }

  @Test
  fun `Dialog is shown when showConfirmationDialog is true`() {
    val showDialogState = mutableStateOf(true)
    val user = User("", "", "", 0, emptyList(), "", parks = emptyList())
    composeTestRule.setContent {
      ConfirmImageDialogWrapper(
          mock(), testImageFile, testImageFile.toUri(), showDialogState, mock(), Park(), user)
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ConfirmImageDialog").assertIsDisplayed()
  }

  @Test
  fun `ConfirmImageDialogWrapper calls uploadImage on confirmation`() {
    val showDialogState = mutableStateOf(true)
    val park = Park(pid = "parkId")
    val user = User("userId", "", "", 0, emptyList(), "", parks = emptyList())
    doNothing().whenever(imageViewModel).uploadImage(any(), any(), any(), any(), any(), any())

    composeTestRule.setContent {
      ConfirmImageDialogWrapper(
        mock(), testImageFile, testImageFile.toUri(), showDialogState, imageViewModel, park, user)
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("uploadButton").performClick()
    verify(imageViewModel)
        .uploadImage(any(), eq(testImageFile), eq(park.pid), eq(user.uid), any(), any())
  }
}
