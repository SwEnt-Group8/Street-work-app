package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrainParamScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun timerInputGrid_DisplaysCorrectButtons() {
    composeTestRule.setContent {
      TimerInputGrid(minutes = 0, seconds = 0, onUpdateMinutes = {}, onUpdateSeconds = {})
    }

    // Verify that all buttons in the timer input grid are displayed
    val buttonChars = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "00", "0", "⌫")
    buttonChars.forEach { char -> composeTestRule.onNodeWithTag("Button$char").assertIsDisplayed() }
  }

  @Test
  fun numberPicker_DisplaysCorrectLabel() {
    composeTestRule.setContent {
      NumberPicker(label = "Number of sets:", value = 5, range = 0..100, onValueChange = {})
    }

    // Verify that the label text is displayed
    composeTestRule.onNodeWithText("Number of sets:").assertIsDisplayed()
  }

  @Test
  fun trainParamHeader_DisplaysCorrectTextForTimeDependent() {
    composeTestRule.setContent {
      TrainParamHeader(
          isTimeDependent = true,
          minutes = 5,
          seconds = 30,
          sets = 0,
          reps = 0,
          activity = "Running")
    }

    // Verify that the header displays the correct text
    composeTestRule
        .onNodeWithText("I want to do 05 min and 30 seconds of Running")
        .assertIsDisplayed()
  }

  @Test
  fun trainParamHeader_DisplaysCorrectTextForNonTimeDependent() {
    composeTestRule.setContent {
      TrainParamHeader(
          isTimeDependent = false,
          minutes = 0,
          seconds = 0,
          sets = 3,
          reps = 10,
          activity = "Push-ups")
    }

    // Verify that the header displays the correct text
    composeTestRule.onNodeWithText("I want to do  3 sets of 10 Push-ups").assertIsDisplayed()
  }

  @Test
  fun setsAndRepsSection_DisplaysPickers() {
    composeTestRule.setContent {
      SetsAndRepsSection(sets = 3, reps = 10, onUpdateSets = {}, onUpdateReps = {})
    }

    // Verify that both sets and reps pickers are displayed
    composeTestRule.onNodeWithTag("SetsPicker").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RepsPicker").assertIsDisplayed()
  }
}
