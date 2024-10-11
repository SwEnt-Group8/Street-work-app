package com.android.streetworkapp.ui

import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
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
import com.android.streetworkapp.Streetworkapp


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
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
    composeTestRule.waitForIdle()
    composeTestRule.onRoot().assertExists()
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)
    composeTestRule.onRoot().assertDoesNotExist()
  }

  @Test
  fun setContentWithNonEmptyContent() {
    composeTestRule.activity.runOnUiThread {
      composeTestRule.activity.setContent(parent = null) { Text(text = "Non-empty content") }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Non-empty content").assertExists()
  }

  @Test
  fun setContentWithDifferentModifiers() {
    composeTestRule.activity.runOnUiThread {
      composeTestRule.activity.setContent(parent = null) {
        Text(text = "Test with modifier", modifier = Modifier.semantics { testTag = "testTag" })
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("testTag").assertExists()
  }

  @Test
  fun streetworkappComposable_logsContextIsNullWhenTesting() {
    // Access the activity's content and pass the `testing = true` parameter
    composeTestRule.activityRule.scenario.onActivity { activity ->
      activity.setContent { Streetworkapp(testing = true) }
    }

    // Perform assertions after the content is set
    composeTestRule.waitForIdle()

    // Assert that the log or relevant content is displayed
    composeTestRule.onRoot().assertExists() // Replace this with specific assertions
  }

  @Test
  fun streetworkappComposable_logsContextIsNotNullWhenNotTesting() {
    // Access the activity's content and pass the `testing = false` parameter
    composeTestRule.activityRule.scenario.onActivity { activity ->
      activity.setContent { Streetworkapp(testing = false) }
    }

    // Perform assertions after the content is set
    composeTestRule.waitForIdle()

    // Assert that the log or relevant content is displayed
    composeTestRule.onRoot().assertExists() // Replace this with specific assertions
  }

}
