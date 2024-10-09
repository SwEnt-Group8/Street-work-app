package com.android.streetworkapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class TinyComposableTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun test() {
    composeTestRule.setContent { TinyComposable() }
    composeTestRule.onNodeWithTag("Hello, World!").assertIsDisplayed()
  }
}
