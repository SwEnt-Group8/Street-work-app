package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent { ProfileScreen() }

    composeTestRule.onNodeWithTag("ProfileScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileScore").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileAddButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileTrainButton").assertIsDisplayed()
  }

  @Test
  fun textCorrectlyDisplayed() {
    composeTestRule.setContent { ProfileScreen() }

    composeTestRule.onNodeWithTag("profileScore").assertTextEquals("Score: 42’424")
    composeTestRule.onNodeWithTag("profileAddButton").assertTextEquals("Add a new friend")
    composeTestRule.onNodeWithTag("profileTrainButton").assertTextEquals("Train with a friend")
  }

  @Test
  fun buttonWork() {
    composeTestRule.setContent { ProfileScreen() }

    composeTestRule.onNodeWithTag("profileAddButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("profileTrainButton").assertHasClickAction()
  }
}
