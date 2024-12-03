package com.android.streetworkapp.ui.train

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimatedCounterTest {

  // Constants
  private val INITIAL_COUNT = 123
  private val UPDATED_COUNT = 456
  private val UPDATED_COUNT_ANIMATION = 125

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun animatedCounter_displaysInitialValue() {
    composeTestRule.setContent { AnimatedCounter(count = INITIAL_COUNT) }

    // Verify that each digit of the initial count is displayed
    verifyDisplayedCount(INITIAL_COUNT)
  }

  @Test
  fun animatedCounter_updatesOnCountChange() {
    // State to simulate count changes
    val countState = mutableStateOf(INITIAL_COUNT)

    composeTestRule.setContent { AnimatedCounter(count = countState.value) }

    // Verify initial value is displayed
    verifyDisplayedCount(INITIAL_COUNT)

    // Update the count state
    composeTestRule.runOnUiThread { countState.value = UPDATED_COUNT }

    // Verify updated value is displayed
    verifyDisplayedCount(UPDATED_COUNT)
  }

  @Test
  fun animatedCounter_handlesAnimationDuringUpdate() {
    // State to simulate count changes
    val countState = mutableStateOf(INITIAL_COUNT)

    composeTestRule.setContent { AnimatedCounter(count = countState.value) }

    // Update the count state
    composeTestRule.runOnUiThread { countState.value = UPDATED_COUNT_ANIMATION }

    // Check if the new digits are displayed after update
    verifyDisplayedCount(UPDATED_COUNT_ANIMATION)
  }

  private fun verifyDisplayedCount(count: Int) {
    count.toString().forEach { digit ->
      composeTestRule.onNodeWithText(digit.toString()).assertIsDisplayed()
    }
  }
}
