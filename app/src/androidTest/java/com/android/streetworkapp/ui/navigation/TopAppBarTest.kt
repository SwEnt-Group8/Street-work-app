package com.android.streetworkapp.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventStatus
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageRepository
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.preferences.PreferencesRepository
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock

class TopAppBarTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var eventRepository: EventRepository
  private lateinit var eventViewModel: EventViewModel

  private val event =
      Event("1", "title", "", 1, 2, Timestamp.now(), "123", emptyList(), "123", EventStatus.CREATED)

  @Before
  fun setUp() {
    eventRepository = mock(EventRepository::class.java, RETURNS_DEFAULTS)
    eventViewModel = EventViewModel(eventRepository)
  }

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
    eventViewModel.setCurrentEvent(event)
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
          eventViewModel,
          ProgressionViewModel(mock(ProgressionRepository::class.java, RETURNS_DEFAULTS)),
          WorkoutViewModel(mock(WorkoutRepository::class.java, RETURNS_DEFAULTS)),
          TextModerationViewModel(mock(TextModerationRepository::class.java)),
          ImageViewModel(mock(ImageRepository::class.java)),
          PreferencesViewModel(mock(PreferencesRepository::class.java)),
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current),
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
