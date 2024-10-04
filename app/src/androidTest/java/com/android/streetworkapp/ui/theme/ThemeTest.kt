package com.android.streetworkapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertNotEquals
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

  @Test
  fun darkTheme_test() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = true) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(LightColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun lightTheme_test() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = false) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(DarkColorScheme.primary, currentColorScheme.primary)
      }
    }
  }
}
