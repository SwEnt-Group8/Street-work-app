package com.android.streetworkapp.ui.miscellaneous

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenUiTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun splashScreen_isDisplayed() {
    composeTestRule.setContent { SplashScreen() }
    composeTestRule.onNodeWithTag("SplashScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SplashScreenCircularProgressIndicator").assertIsDisplayed()
  }
}
