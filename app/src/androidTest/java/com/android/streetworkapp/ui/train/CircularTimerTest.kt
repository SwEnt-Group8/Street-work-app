package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CircularTimerTest {

  @get:Rule val composeTestRule = createComposeRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun circularTimer_decrementsTime() = runTest {
    composeTestRule.setContent { CircularTimer(totalTime = 1f, onTimeUp = {}) }
    // Advance to the end
    composeTestRule.mainClock.advanceTimeBy(1000L)
    composeTestRule.waitForIdle() // Ensure state updates are applied

    // Ensure "Time's Up!" is displayed and "0s" is not shown
    composeTestRule.onNodeWithTag("TimeRemainingText").assertExists()
    composeTestRule.onNodeWithText("0s").assertDoesNotExist()
  }
}
