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
}
