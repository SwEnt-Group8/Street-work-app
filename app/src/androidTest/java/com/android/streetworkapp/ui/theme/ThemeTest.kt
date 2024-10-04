package com.android.streetworkapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertEquals
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
      SampleAppTheme(darkTheme = false, dynamicColor = false) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertEquals(LightColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun lightThemedynamic_test() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = false, dynamicColor = true) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(LightColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun darkTheme2_test() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = true, dynamicColor = false) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertEquals(DarkColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun darkThemedynamic_test() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = true, dynamicColor = true) {
        val currentColorScheme = MaterialTheme.colorScheme
        assertNotEquals(DarkColorScheme.primary, currentColorScheme.primary)
      }
    }
  }

  @Test
  fun dynamicColor_onAndroid11() {
    composeTestRule.setContent {
      SampleAppTheme(dynamicColor = true) { Text("Test Dynamic Colors on Android 11") }
    }
    composeTestRule.onNodeWithText("Test Dynamic Colors on Android 11").assertExists()
  }

  @Test
  fun lightTheme_dynamicColor_onAndroid12() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = false, dynamicColor = true) {
        Text("Light Theme with Dynamic Colors on Android 12")
      }
    }
    composeTestRule.onNodeWithText("Light Theme with Dynamic Colors on Android 12").assertExists()
  }

  @Test
  fun darkTheme_dynamicColor_onAndroid12() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = true, dynamicColor = true) {
        Text("Dark Theme with Dynamic Colors on Android 12")
      }
    }
    composeTestRule.onNodeWithText("Dark Theme with Dynamic Colors on Android 12").assertExists()
  }

  @Test
  fun lightTheme_noDynamicColor() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = false, dynamicColor = false) {
        Text("Light Theme without Dynamic Colors")
      }
    }
    composeTestRule.onNodeWithText("Light Theme without Dynamic Colors").assertExists()
  }

  @Test
  fun darkTheme_noDynamicColor() {
    composeTestRule.setContent {
      SampleAppTheme(darkTheme = true, dynamicColor = false) {
        Text("Dark Theme without Dynamic Colors")
      }
    }
    composeTestRule.onNodeWithText("Dark Theme without Dynamic Colors").assertExists()
  }
}
