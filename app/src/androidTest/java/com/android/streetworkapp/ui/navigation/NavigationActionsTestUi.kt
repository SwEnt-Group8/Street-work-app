package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.StreetWorkAppMain
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationActionsTestUi {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun navigatingToAnInvalidRouteDoesNotThrowAnException() {
    val invalidRouteName = "nEzUaeB16f"
    val topLevelDestWithInvalidRouteName =
        TopLevelDestination(
            invalidRouteName, Icons.Outlined.Lock, "dummy") // dummy top level dest for testing
    composeTestRule.setContent {
      StreetWorkAppMain { navigateTo(topLevelDestWithInvalidRouteName) }
    }
  }
}
