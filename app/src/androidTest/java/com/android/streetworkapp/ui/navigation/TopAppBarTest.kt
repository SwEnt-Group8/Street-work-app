package com.android.streetworkapp.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val mockParkList =
        listOf(
            ParkLocation(lat = 46.518659400000004, lon = 6.566561505148001, id = "1"),
            ParkLocation(lat = 34.052235, lon = -118.243683, id = "2"),
            ParkLocation(lat = 51.507351, lon = -0.127758, id = "3"),
            ParkLocation(lat = 35.676192, lon = 139.650311, id = "4"),
            ParkLocation(lat = -33.868820, lon = 151.209290, id = "5"))

    parkLocationRepository = mockk<OverpassParkLocationRepository>()
    every {
      parkLocationRepository.search(
          any<Double>(),
          any<Double>(),
          any<(List<ParkLocation>) -> Unit>(),
          any<(Exception) -> Unit>())
    } answers
        {
          val onSuccess = this.args[2] as (List<ParkLocation>) -> Unit
          onSuccess(mockParkList) // Invoke onSuccess with the custom list
        }

    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
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
    val currentScreenParam =
        mutableStateOf(
            LIST_OF_SCREENS.first()) // can't call setContent twice per test so we use this instead
    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(currentScreenParam.value.screenName) },
          {},
          UserViewModel(mockk<UserRepositoryFirestore>()),
          ParkViewModel(mockk<ParkRepositoryFirestore>()),
          EventViewModel(mockk<EventRepositoryFirestore>()),
          ProgressionViewModel(mockk<ProgressionRepositoryFirestore>()),
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
