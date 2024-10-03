package com.android.sample.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.sample.Greeting
import com.android.sample.resources.C
import com.android.sample.ui.theme.SampleAppTheme
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testGreetingDisplaysCorrectText() {
    // Set the content with the Greeting composable
    composeTestRule.setContent { SampleAppTheme { Greeting(name = "Android") } }

    // Assert that the text is displayed correctly
    composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
  }

  @Test
  fun testMainScreenContainerHasCorrectTestTag() {
    composeTestRule.setContent {
      SampleAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              Greeting("Android")
            }
      }
    }

    // Verify that the main screen container has the correct test tag
    composeTestRule.onNodeWithTag(C.Tag.main_screen_container).assertExists()
  }

  @Test
  fun testGreetingHasCorrectTestTag() {
    composeTestRule.setContent { Greeting(name = "Android") }

    // Verify that the greeting has the correct test tag
    composeTestRule.onNodeWithTag(C.Tag.greeting).assertExists()
  }
}
