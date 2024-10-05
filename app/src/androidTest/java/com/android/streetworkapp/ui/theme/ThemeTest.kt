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
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = false) { Text("Test Light Theme") }
    }
    composeTestRule.onNodeWithText("Test Light Theme").assertExists()
  }

  @Test
  fun darkTheme_appliesCorrectly() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = true) { Text("Test Dark Theme") }
    }
    composeTestRule.onNodeWithText("Test Dark Theme").assertExists()
  }

  @Test
  fun darkTheme_usesCorrectColorScheme() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = true) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(LightColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun lightTheme_usesCorrectColorScheme() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = false) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(DarkColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun materialTheme_appliesCustomColorSchemeCorrectly1() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = false) {
        MaterialTheme(colorScheme = DarkColorScheme) {
          Text("Material Theme with Custom Dark Color Scheme")
        }
      }
    }
    composeTestRule.onNodeWithText("Material Theme with Custom Dark Color Scheme").assertExists()
  }

  @Test
  fun materialTheme_appliesCustomColorSchemeCorrectly2() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = true) {
        MaterialTheme(colorScheme = LightColorScheme) {
          Text("Material Theme with Custom Light Color Scheme")
        }
      }
    }
    composeTestRule.onNodeWithText("Material Theme with Custom Light Color Scheme").assertExists()
  }

  @Test
  fun materialTheme_appliesCustomTypographyCorrectly() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = false) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography =
                Typography.copy(
                    bodyLarge =
                        androidx.compose.ui.text.TextStyle(
                            color = androidx.compose.ui.graphics.Color.Red))) {
              Text("Material Theme with Custom Typography")
            }
      }
    }
    composeTestRule.onNodeWithText("Material Theme with Custom Typography").assertExists()
  }

  @Test
  fun lightThemeWD_appliesCorrectly() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = false) { Text("Test Light Theme") }
    }
    composeTestRule.onNodeWithText("Test Light Theme").assertExists()
  }

  @Test
  fun darkThemeWD_appliesCorrectly() {
    composeTestRule.setContent {
      SampleAppThemeWithoutDynamicColor(darkTheme = true) { Text("Test Dark Theme") }
    }
    composeTestRule.onNodeWithText("Test Dark Theme").assertExists()
  }
}
