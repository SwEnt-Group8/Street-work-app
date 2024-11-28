package com.android.streetworkapp.ui.tutorial

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.android.streetworkapp.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class TutorialEventTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // start tutorial screen
    composeTestRule.setContent { TutorialEvent(navigationActions) }
  }

  @Test
  fun testTutorial() {

    // Assert the box and column of the screen display
    composeTestRule.onNodeWithTag("tutoScreenBoxContainer").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoScreenColumnContainer").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoPageContainer").assertExists().assertIsDisplayed()

    // Assert each Pager is displayed

    // Check all component on the current page is displayed
    tutoComponentsDisplayedOnPage1()

    // Simulate swipe to the left to move from page 1 -> page 2
    composeTestRule.onNodeWithTag("tutoPageContainer").performTouchInput {
      swipe(
          start = Offset(600f, 500f), // Start position
          end = Offset(100f, 500f) // End position
          )
    }

    // Wait for UI
    composeTestRule.waitForIdle()

    // Check all component on the current page is displayed
    tutoComponentsDisplayedOnPage2()

    // Simulate swipe to the left to move from page 2 -> page 3
    composeTestRule.onNodeWithTag("tutoPageContainer").performTouchInput {
      swipe(
          start = Offset(600f, 500f), // Start position
          end = Offset(100f, 500f) // End position
          )
    }

    // Wait for UI
    composeTestRule.waitForIdle()

    // Check all component on the current page is displayed
    tutoComponentsDisplayedOnPage3()

    // Simulate swipe to the left to move from page 3 -> page 4
    composeTestRule.onNodeWithTag("tutoPageContainer").performTouchInput {
      swipe(
          start = Offset(600f, 500f), // Start position
          end = Offset(100f, 500f) // End position
          )
    }

    // Wait for UI
    composeTestRule.waitForIdle()

    // Check all component on the current page is displayed
    tutoComponentsDisplayedOnPage4()

    // Simulate swipe to the left to move from page 4 -> page 5
    composeTestRule.onNodeWithTag("tutoPageContainer").performTouchInput {
      swipe(
          start = Offset(600f, 500f), // Start position
          end = Offset(100f, 500f) // End position
          )
    }

    // Wait for UI
    composeTestRule.waitForIdle()

    // Check all component on the current page is displayed
    tutoComponentsDisplayedOnEndingPage()
  }

  private fun tutoComponentsDisplayedOnPage1() {
    // Verify element of page 1 are displayed
    composeTestRule.onNodeWithTag("tutoColumn0").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoText0").assertExists().assertIsDisplayed()

    // UX - Text values for page 1:
    composeTestRule.onNodeWithTag("tutoText0").assertTextEquals("Welcome to Street WorkApp!")
  }

  private fun tutoComponentsDisplayedOnPage2() {

    // Verify element of page 2 are displayed
    composeTestRule.onNodeWithTag("tutoColumn1").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoImage1").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoText1").assertExists().assertIsDisplayed()

    // UX - Text values for page 2:
    composeTestRule.onNodeWithTag("tutoText1").assertTextEquals("Click on your favorite park")
  }

  private fun tutoComponentsDisplayedOnPage3() {

    // Verify element of page 3 are displayed
    composeTestRule.onNodeWithTag("tutoColumn2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoImage2").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoText2").assertExists().assertIsDisplayed()

    // UX - Text values for page 3:
    composeTestRule
        .onNodeWithTag("tutoText2")
        .assertTextEquals("Look for others events in the park\n" + "Or start one yourself")
  }

  private fun tutoComponentsDisplayedOnPage4() {

    // Verify element of page 4 are displayed
    composeTestRule.onNodeWithTag("tutoColumn3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoImage3").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoText3").assertExists().assertIsDisplayed()

    // UX - Text values for page 4:
    composeTestRule.onNodeWithTag("tutoText3").assertTextEquals("Join an already ongoing event")
  }

  private fun tutoComponentsDisplayedOnEndingPage() {

    // Verify element of page 5 are displayed
    composeTestRule.onNodeWithTag("tutoColumn4").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoImage4").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoText4").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("tutoButtonBox").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tutoButton")
        .assertExists()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule
        .onNodeWithTag("tutoButtonRow", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("tutoCloseButtonText", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    // UX - Text values for page 5:
    composeTestRule.onNodeWithTag("tutoText4").assertTextEquals("Or create your own event")
    // UX - Text values for page 5:
    composeTestRule
        .onNodeWithTag("tutoCloseButtonText", useUnmergedTree = true)
        .assertTextEquals("Close")
  }
}
