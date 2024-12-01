package com.android.streetworkapp.ui.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ConfirmImageDialogTest {

    @Mock
    private lateinit var mockContext: Context
    @Mock
    private lateinit var mockContentResolver: ContentResolver
    @Mock
    private lateinit var mockUri: Uri
    @Mock
    private lateinit var mockInputStream: InputStream

    private lateinit var testImageFile: File

    private lateinit var context: Context

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        testImageFile = temporaryFolder.newFile("test_confirm_image_dialog.jpg")
        FileOutputStream(testImageFile).use { outputStream ->
            val dummyData = ByteArray(100) { 0xFF.toByte() } // write dummy data to file
            outputStream.write(dummyData)
        }
    }

    @Test
    fun componentsAreDisplayedCorrectly() {
        composeTestRule.setContent {
            ConfirmImageDialog(
                imageUri = testImageFile.toUri(),
                onConfirm = {},
                onCancel = {},
                context = context
            )
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
                onCancel = { onCancel.run() },
                context = context
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("cancelButton").performClick()
        verify(onCancel).run()

        composeTestRule.onNodeWithTag("uploadButton").performClick()
        verify(onConfirm).run()
    }

}