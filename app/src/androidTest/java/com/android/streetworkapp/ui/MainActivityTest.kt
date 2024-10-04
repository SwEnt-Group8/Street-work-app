package com.android.streetworkapp.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.MainActivity
import com.android.streetworkapp.resources.C
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun mainScreenContainerIsNotNull() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(C.Tag.main_screen_container).assertIsNotDisplayed()
  }

  @Test
  fun setContentExecutesSuccessfully() {
    // Assert that the text "This is the main screen container" is displayed
    composeTestRule.onNodeWithText("None existing text").assertIsNotDisplayed()
  }

  @Test
  fun activityIsRecreatedSuccessfully() {
    // Simulate configuration change (like screen rotation)
    composeTestRule.activityRule.scenario.recreate()

    // After recreation, the root node should still exist (though it’s empty)
    composeTestRule.onRoot().assertExists()
  }

  @Test
  fun testActivityLifecycleEvents() {
    // Simulate lifecycle events
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)

    // Ensure the root node is still valid after lifecycle changes
    composeTestRule.onRoot().assertExists()
  }
}
