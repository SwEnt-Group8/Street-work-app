package com.android.streetworkapp.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun UIComponentsDisplayed() {
    // Test uses useUnmerged = true for all children of containers,
    // otherwise will not be accessible for the test using testTags.

    // For Box, Text, Image, Buttons, List : check if displayed :
    composeTestRule.onNodeWithTag("loginScreenBoxContainer").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loginButtonIcon", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginButtonText", useUnmergedTree = true).assertIsDisplayed()

    // For columns / rows / spacers : check if exist :
    composeTestRule.onNodeWithTag("loginScreenColumnContainer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginButtonRowContainer", useUnmergedTree = true).assertExists()

    // UX elements :

    // UX - Text values :
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome to the Street Work'App")
    composeTestRule.onNodeWithTag("loginButton").assertTextEquals("Sign in with Google")
    composeTestRule
        .onNodeWithTag("loginButtonText", useUnmergedTree = true)
        .assertTextEquals("Sign in with Google")

    // UX - Button click action :
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }
}
