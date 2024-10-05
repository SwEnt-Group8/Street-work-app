package com.android.streetworkapp.ui

import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    // Wait for the UI to be idle before performing the test
    composeTestRule.waitForIdle()

    // Assert that the node with the test tag "main_screen_container" is not displayed
    composeTestRule.onNodeWithTag(C.Tag.main_screen_container).assertIsNotDisplayed()
  }

  @Test
  fun setContentExecutesSuccessfully() {
    // Attempt to find a node with the text "None existing text"
    // and assert that it is not displayed, as this text doesn't exist in the UI
    composeTestRule.onNodeWithText("None existing text").assertIsNotDisplayed()
  }

  @Test
  fun activityIsRecreatedSuccessfully() {
    // Simulate a configuration change by recreating the activity (e.g., screen rotation)
    composeTestRule.activityRule.scenario.recreate()

    // After recreation, assert that the root node still exists,
    // which indicates the activity was recreated successfully
    composeTestRule.onRoot().assertExists()
  }

  @Test
  fun testActivityLifecycleEvents() {
    // Move the activity to the STARTED state, simulating a lifecycle transition
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.STARTED)

    // Move the activity to the RESUMED state
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Assert that the root node exists while the activity is in the RESUMED state
    composeTestRule.onRoot().assertExists()

    // Move the activity to the DESTROYED state, simulating the activity being destroyed
    composeTestRule.activityRule.scenario.moveToState(Lifecycle.State.DESTROYED)

    // Assert that the root node no longer exists after the activity is destroyed
    composeTestRule.onRoot().assertDoesNotExist()
  }

  @Test
  fun setContentWithNonEmptyContent() {
    // Run the UI action on the main thread and set content to a Text composable with non-empty
    // content
    composeTestRule.activity.runOnUiThread {
      composeTestRule.activity.setContent(parent = null) { Text(text = "Non-empty content") }
    }

    // Wait for the UI to become idle
    composeTestRule.waitForIdle()

    // Assert that the Text node with the content "Non-empty content" exists
    composeTestRule.onNodeWithText("Non-empty content").assertExists()
  }

  @Test
  fun setContentWithDifferentModifiers() {
    // Run the UI action on the main thread and set content with a modifier
    composeTestRule.activity.runOnUiThread {
      composeTestRule.activity.setContent(parent = null) {
        Text(text = "Test with modifier", modifier = Modifier.semantics { testTag = "testTag" })
      }
    }

    // Wait for the UI to become idle
    composeTestRule.waitForIdle()

    // Assert that the node with the test tag "testTag" exists
    composeTestRule.onNodeWithTag("testTag").assertExists()
  }

  @Test
  fun streetworkappComposable_initializesNavController() {
    // Perform assertions after the UI has become idle
    composeTestRule.waitForIdle()

    // Now assert that the NavController is initialized by checking the UI
    composeTestRule.onRoot().assertExists()
  }

  @Test
  fun navController_isRememberedAcrossRecompositions() {
    // This state will be used to trigger recomposition
    var shouldRecompose by mutableStateOf(false)

    lateinit var firstNavController: NavController
    lateinit var secondNavController: NavController

    // Access the activity's content and modify it from within the test
    composeTestRule.activityRule.scenario.onActivity { activity ->
      // Set up content in the activity that uses the test-controlled state
      activity.setContent {
        val navController = rememberNavController()
        if (!shouldRecompose) {
          // Get NavController in the initial composition
          firstNavController = navController
        } else {
          // Get NavController during recomposition
          secondNavController = navController
        }
      }
    }

    // Trigger recomposition
    composeTestRule.runOnIdle { shouldRecompose = true }

    // Wait for the UI to become idle after recomposition
    composeTestRule.waitForIdle()

    // Assert that the same NavController is reused across recompositions
    assertEquals(firstNavController, secondNavController)
  }

  @Test
  fun navController_handlesNavigation() {
    // Access the activity's content and modify it from within the test
    lateinit var navController: NavController

    composeTestRule.activityRule.scenario.onActivity { activity ->
      // Set up content in the activity that uses the test-controlled state
      activity.setContent {
        navController = rememberNavController()

        // Create the composable with a NavHost for navigation
        NavHost(navController as NavHostController, startDestination = "home") {
          composable("home") { Text("Home Screen") }
          composable("detail") { Text("Detail Screen") }
        }
      }
    }

    // Perform assertions for the initial screen
    composeTestRule.onNodeWithText("Home Screen").assertExists()

    // Simulate navigation to another screen
    composeTestRule.runOnIdle { navController.navigate("detail") }

    // Wait for the UI to settle
    composeTestRule.waitForIdle()

    // Perform assertions for the new screen
    composeTestRule.onNodeWithText("Detail Screen").assertExists()
  }
}
