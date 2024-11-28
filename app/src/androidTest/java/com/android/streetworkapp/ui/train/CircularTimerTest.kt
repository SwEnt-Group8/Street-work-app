package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CircularTimerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun circularTimer_decrementsTime() = runTest {
    var timeUpCalled = false
    //Small timer for testing
    composeTestRule.setContent {
      CircularTimer(totalTime = 3, onTimeUp = { timeUpCalled = true })
    }

    // Initial state
    composeTestRule.onNodeWithText("3s").assertExists()

    // Simulate 1-second intervals
    composeTestRule.mainClock.advanceTimeBy(1000L)
    composeTestRule.onNodeWithText("2s").assertExists()

    composeTestRule.mainClock.advanceTimeBy(1000L)
    composeTestRule.onNodeWithText("1s").assertExists()

    // Advance to the end
    composeTestRule.mainClock.advanceTimeBy(1000L)

    // Ensure "Time's Up!" is displayed and "0s" is not shown
    composeTestRule.onNodeWithText("Time's Up!").assertExists()
    composeTestRule.onNodeWithText("0s").assertDoesNotExist()

    // Ensure onTimeUp callback is triggered
    assertTrue(timeUpCalled)
  }

  @Test
  fun circularTimer_onTimeUpInvoked() = runTest {
    var callbackInvoked = false

    composeTestRule.setContent {
      CircularTimer(totalTime = 1, onTimeUp = { callbackInvoked = true })
    }

    // Wait for the timer to complete
    composeTestRule.mainClock.advanceTimeBy(1000L)
    composeTestRule.awaitIdle()

    // Verify that the callback is invoked
    assertTrue("Callback should have been invoked", callbackInvoked)
  }
}
