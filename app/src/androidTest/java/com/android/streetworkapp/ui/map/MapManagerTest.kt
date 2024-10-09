package com.android.streetworkapp.ui.map

import android.Manifest
import androidx.activity.compose.setContent
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

class MapManagerTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

  @Before
  fun setup() {
    // Mock the ActivityResultLauncher using Mockito
    requestPermissionLauncher = mock()
  }

  @Test
  fun buttonClick() {
    // Set the content of the activity
    composeTestRule.setContent {
      MapPermission(requestPermissionLauncher = requestPermissionLauncher)
    }

    // The first request
    composeTestRule.onNodeWithTag("requestPermissionButton").assertIsDisplayed()
    verify(requestPermissionLauncher, times(1)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    // The first request second request if pressed
    composeTestRule.onNodeWithTag("requestPermissionButton").performClick()
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()

    // Verify that the launch method of requestPermissionLauncher was called with the correct
    verify(requestPermissionLauncher, times(2)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }

  @Test
  fun buttonNotClick() {
    verify(requestPermissionLauncher, times(0)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    // Set the content of the activity
    composeTestRule.setContent {
      MapPermission(requestPermissionLauncher = requestPermissionLauncher)
    }

    // The first request
    composeTestRule.onNodeWithTag("requestPermissionButton").assertIsDisplayed()
    verify(requestPermissionLauncher, times(1)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()

    // Verify that the launch method of requestPermissionLauncher was called with the correct
    verify(requestPermissionLauncher, times(1)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }

  /*@Test
  fun testPermissionAlreadyGranted() {
    // Simulate the permission already granted
    val context = composeTestRule.activity
    composeTestRule.activityRule.scenario.onActivity { activity ->
      ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION).let {
        PackageManager.PERMISSION_GRANTED
      }

      activity.setContent { MapPermission(requestPermissionLauncher = requestPermissionLauncher) }
    }

    // Verify that the requestPermissionLauncher is NOT called automatically
    verify(requestPermissionLauncher, times(0)).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    // Verify the button is displayed with "Test Permission"
    composeTestRule.onNodeWithText("Test Permission").assertIsDisplayed()
  }*/
}
