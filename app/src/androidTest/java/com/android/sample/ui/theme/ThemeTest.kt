package com.android.sample.ui.theme

import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class BootcampThemeTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun lightTheme_appliesCorrectly() {
    composeTestRule.setContent { SampleAppTheme(darkTheme = false) { Text("Test Light Theme") } }
    composeTestRule.onNodeWithText("Test Light Theme").assertExists()
  }

  @Test
  fun darkTheme_appliesCorrectly() {
    composeTestRule.setContent { SampleAppTheme(darkTheme = true) { Text("Test Dark Theme") } }
    composeTestRule.onNodeWithText("Test Dark Theme").assertExists()
  }

  @Test
  fun dynamicColor_onAndroid12() {
    composeTestRule.setContent {
      SampleAppTheme(dynamicColor = true) { Text("Test Dynamic Colors") }
    }
  }
}
