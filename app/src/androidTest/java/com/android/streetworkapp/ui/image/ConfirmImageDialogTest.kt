package com.android.streetworkapp.ui.image

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class ConfirmImageDialogTest {
  private lateinit var testImageFile: File

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
  fun componentsAreDisplayedCorrectly() {
    composeTestRule.setContent {
      ConfirmImageDialog(imageUri = testImageFile.toUri(), onConfirm = {}, onCancel = {})
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("dialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("disclaimerText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancelButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("uploadButton").assertIsDisplayed()
  }

  @Test
  fun cancelAndUploadButtonsCallCorrectFunctions() {
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
}
