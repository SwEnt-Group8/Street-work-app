package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest : TestCase() {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // Mock the current route to be the add profile screen
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
    composeTestRule.setContent { ProfileScreen(navigationActions) }
  }

  @Test
  fun textCorrectlyDisplayed() {
    composeTestRule.waitForIdle() // Wait for rendering
    composeTestRule.onNodeWithTag("profileScore").assertTextEquals("Score: 42’424")
    composeTestRule.onNodeWithTag("profileAddButton").assertTextEquals("Add a new friend")
    composeTestRule.onNodeWithTag("profileTrainButton").assertTextEquals("Train with a friend")
  }

  @Test
  fun buttonWork() {
    composeTestRule.waitForIdle() // Wait for rendering
    composeTestRule.onNodeWithTag("profileAddButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("profileTrainButton").assertHasClickAction()
  }
}
