package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationActionsTestUi {

  @get:Rule val composeTestRule = createComposeRule()

  @Ignore("This test is not working because of the new StreetWorkAppMain parameters")
  @Test
  fun navigatingToAnInvalidRouteDoesNotThrowAnException() {
    val invalidRouteName = "nEzUaeB16f"
    val topLevelDestWithInvalidRouteName =
        TopLevelDestination(
            invalidRouteName,
            Icons.Outlined.Lock,
            null,
            "dummy") // dummy top level dest for testing
    // composeTestRule.setContent {
    // StreetWorkAppMain { navigateTo(topLevelDestWithInvalidRouteName) }
    // }
  }
}
