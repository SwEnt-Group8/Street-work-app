package com.android.streetworkapp.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock

class TopAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun changingTitleInManagerMakesItChangeOnScreen() {
    val topAppBarManager = TopAppBarManager("old title")

    topAppBarManager.setTopAppBarTitle("new title")
    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }
    composeTestRule
        .onNodeWithTag("topAppBarTitle")
        .assertIsDisplayed()
        .assertTextEquals("new title")
  }

  @Test
  fun isDisplayedCorrectlyOnScreens() {
    val currentScreenParam =
        mutableStateOf(
            LIST_OF_SCREENS.first()) // can't call setContent twice per test so we use this instead
    composeTestRule.setContent {
      StreetWorkApp(
          ParkLocationViewModel(mock(ParkLocationRepository::class.java, RETURNS_DEFAULTS)),
          { navigateTo(currentScreenParam.value.screenName) },
          {},
          UserViewModel(mock(UserRepository::class.java, RETURNS_DEFAULTS)),
          ParkViewModel(mock(ParkRepository::class.java, RETURNS_DEFAULTS)),
          EventViewModel(mock(EventRepository::class.java, RETURNS_DEFAULTS)),
          ProgressionViewModel(mock(ProgressionRepository::class.java, RETURNS_DEFAULTS)),
          WorkoutViewModel(mock(WorkoutRepository::class.java, RETURNS_DEFAULTS)),
          TextModerationViewModel(mock(TextModerationRepository::class.java)),
          true)
    }

    for (screenParam in LIST_OF_SCREENS) {
      if (screenParam.screenName in TEST_SCREEN_EXCLUSION_LIST) continue

      currentScreenParam.value = screenParam // Update the state

      composeTestRule.waitForIdle()
      if (screenParam.isTopBarVisible) {
        composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
        screenParam.topAppBarManager?.let { topAppBarManager ->
          if (topAppBarManager.hasNavigationIcon())
              composeTestRule.onNodeWithTag("goBackButtonTopAppBar").assertIsDisplayed()
        }
      } else composeTestRule.onNodeWithTag("topAppBar").assertIsNotDisplayed()
    }
  }
}
