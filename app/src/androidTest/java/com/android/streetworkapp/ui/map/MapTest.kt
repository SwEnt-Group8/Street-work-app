package com.android.streetworkapp.ui.map

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

private const val PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

class MapTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  @Before
  fun setup() {
    // Mock the ActivityResultLauncher using Mockito
    requestPermissionLauncher = mock()

    verify(requestPermissionLauncher, times(0)).launch(PERMISSION_LOCATION)
    // Set the content of the activity
    composeTestRule.setContent { MapManager(requestPermissionLauncher = requestPermissionLauncher) }
  }

  @Test
  fun buttonClick() {
    // The first request
    composeTestRule.onNodeWithTag("requestPermissionButton").assertIsDisplayed()
    verify(requestPermissionLauncher, times(1)).launch(PERMISSION_LOCATION)
    // The first request second request if pressed
    composeTestRule.onNodeWithTag("requestPermissionButton").performClick()
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()

    // Verify that the launch method of requestPermissionLauncher was called with the correct
    verify(requestPermissionLauncher, times(2)).launch(PERMISSION_LOCATION)
  }

  @Test
  fun buttonNotClick() {
    // The first request
    composeTestRule.onNodeWithTag("requestPermissionButton").assertIsDisplayed()
    verify(requestPermissionLauncher, times(1)).launch(PERMISSION_LOCATION)
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()

    // Verify that the launch method of requestPermissionLauncher was called with the correct
    verify(requestPermissionLauncher, times(1)).launch(PERMISSION_LOCATION)
  }
}
