package com.android.streetworkapp.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
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
  fun uiComponentsDisplayed() {
    composeTestRule.waitForIdle()
    // Test uses useUnmerged = true for all children of containers,
    // otherwise will not be accessible for the test using testTags.

    // For Box, Text, Image, Buttons, List : check if displayed :
    composeTestRule.onNodeWithTag("loginScreenBoxContainer").assertExists().assertIsDisplayed()
    print("loginScreenBoxContainer")
    composeTestRule.onNodeWithTag("loginScreenAppLogo").assertExists().assertIsDisplayed()
    print("loginScreenAppLogo")
    composeTestRule.onNodeWithTag("loginScreenFirstRowIcon").assertExists().assertIsDisplayed()
    print("loginScreenFirstRowIcon")
    composeTestRule.onNodeWithTag("loginButton").assertExists().assertIsDisplayed()
    print("loginButton")
    composeTestRule
        .onNodeWithTag("loginButtonIcon", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    print("loginButtonIcon")

    // For Pager component
    composeTestRule.onNodeWithTag("introScreenBoxContainer").assertExists().assertIsDisplayed()
    print("introScreenBoxContainer")
    composeTestRule.onNodeWithTag("introBox1").assertExists().assertIsDisplayed()
    print("introBox1")
    composeTestRule.onNodeWithTag("introImage1").assertExists().assertIsDisplayed()
    print("introImage1")
    composeTestRule.onNodeWithTag("introApp1").assertExists().assertIsDisplayed()
    print("introApp1")
    composeTestRule.onNodeWithTag("introDotRow").assertExists().assertIsDisplayed()
    print("introDotRow")
    composeTestRule.onNodeWithTag("introColumn1").assertExists().assertIsDisplayed()
    print("introColumn1")

    composeTestRule.onNodeWithTag("loginButtonText", useUnmergedTree = true).assertIsDisplayed()
    print("loginButtonText")
    // For columns / rows / spacers : check if exist :
    composeTestRule.onNodeWithTag("loginScreenColumnContainer").assertExists()
    print("loginScreenColumnContainer")
    composeTestRule.onNodeWithTag("loginScreenFirstSpacer").assertExists()
    print("loginScreenFirstSpacer")
    composeTestRule.onNodeWithTag("loginScreenFirstRow").assertExists().assertIsDisplayed()
    print("loginScreenFirstRow")
    composeTestRule.onNodeWithTag("loginScreenFirstRowSpacer").assertExists()
    print("loginScreenFirstRowSpacer")
    composeTestRule.onNodeWithTag("loginButtonRowContainer", useUnmergedTree = true).assertExists()
    print("loginButtonRowContainer")
    // UX elements :

    // UX - Text values :
    composeTestRule
        .onNodeWithTag("loginScreenFirstRowText")
        .assertTextEquals("Find nearby parks and events to participate in or create")
    print("loginScreenFirstRowText")
    composeTestRule.onNodeWithTag("loginButton").assertTextEquals("Sign in with Google")
    print("loginButton")
    composeTestRule
        .onNodeWithTag("loginButtonText", useUnmergedTree = true)
        .assertTextEquals("Sign in with Google")
    print("loginButtonText")
    // UX - Button click action :
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
    print("loginButton")
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("loginButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun uiComponentsDisplayedOnSecondPage() {

    // Ensure the page 1 is displayed
    composeTestRule.onNodeWithTag("introColumn1").assertExists().assertIsDisplayed()

    // Simulate swipe to the left to move from page 1 -> page 2
    swipeToLeft()

    // Verify element of page 2 are displayed
    composeTestRule.onNodeWithTag("introColumn2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("introBox2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("IntroImage2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("introApp2").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginScreenSecondRowIcon").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginScreenSecondSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenSecondRow").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginScreenSecondRowSpacer").assertExists()

    // UX - Text values for page 2:
    composeTestRule
        .onNodeWithTag("loginScreenSecondRowText")
        .assertTextEquals("Track your activities and learn new skills")
  }

  @Test
  fun uiComponentsDisplayedOnThirdPage() {

    // Ensure the page 1 is displayed
    composeTestRule.onNodeWithTag("introColumn1").assertExists().assertIsDisplayed()

    // Simulate swipe to the left to move from page 1 -> page 2
    swipeToLeft()

    // Ensure the page 2 is displayed
    composeTestRule.onNodeWithTag("introColumn2").assertExists().assertIsDisplayed()

    // Simulate swipe to the left to move from page 2 -> page 3
    swipeToLeft()

    // Verify element of page 3 are displayed
    composeTestRule.onNodeWithTag("introColumn3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("introBox3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("introImage3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("introApp3").assertExists().assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginScreenThirdRowIcon").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginScreenThirdRow").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginScreenThirdRowSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenThirdSpacer").assertExists()

    // UX - Text values for page 3:
    composeTestRule
        .onNodeWithTag("loginScreenThirdRowText")
        .assertTextEquals("Make new friends, train together and share activities")
  }

  @Test
  fun testPagerWork() {

    // Ensure the page 1 is displayed
    composeTestRule.onNodeWithTag("introColumn1").assertExists().assertIsDisplayed()

    // Simulate swipe to the left to move from page 1 -> page 2
    swipeToLeft()

    // Ensure the page 2 is displayed
    composeTestRule.onNodeWithTag("introColumn2").assertExists().assertIsDisplayed()

    // Simulate swipe to the left to move from page 2 -> page 3
    swipeToLeft()

    // Ensure the page 3 is displayed
    composeTestRule.onNodeWithTag("introColumn3").assertExists().assertIsDisplayed()

    // Simulate swipe to the right to move from page 3 -> page 2
    swipeToRight()

    // Ensure the page 2 is still displayed
    composeTestRule.onNodeWithTag("introColumn2").assertExists().assertIsDisplayed()

    // Simulate swipe to the right to move from page 2 -> page 1
    swipeToRight()

    // Assert the page 1 is still displayed
    composeTestRule.onNodeWithTag("introColumn1").assertExists().assertIsDisplayed()
  }

  private fun swipeToLeft() {
    composeTestRule.onNodeWithTag("introScreenBoxContainer").performTouchInput { swipeLeft() }
    composeTestRule.waitForIdle()
  }

  private fun swipeToRight() {
    composeTestRule.onNodeWithTag("introScreenBoxContainer").performTouchInput { swipeRight() }
    composeTestRule.waitForIdle()
  }
}
