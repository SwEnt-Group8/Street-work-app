package com.android.streetworkapp.ui.Progression

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.progress.ProgressScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class ProgressionTest {

  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    composeTestRule.setContent { ProgressScreen(navigationActions) }
  }

  @Test
  fun isScreenDisplayed() {
    composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
  }

  @Test
  fun areAllComponentsDisplayed() {
    composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("circularProgressBar").assertIsDisplayed()
  }
}
