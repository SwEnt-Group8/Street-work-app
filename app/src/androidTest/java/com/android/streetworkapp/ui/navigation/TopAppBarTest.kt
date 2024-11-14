package com.android.streetworkapp.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class TopAppBarTest {

  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel

  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventRepository: EventRepository
  private lateinit var eventViewModel: EventViewModel
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    parkRepository = mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)
    eventRepository = mock(EventRepository::class.java)
    eventViewModel = EventViewModel(eventRepository)
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)

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

    val eventList =
        EventList(
            events =
                listOf(
                    Event(
                        "1",
                        "Group workout",
                        "A fun group workout session to train new skills! \r\n\r\n" +
                            "Come and join the fun of training with other motivated street workers while progressing on your figures\r\n" +
                            "We accept all levels: newcomers welcome\r\n\r\n" +
                            "see https/street-work-app/thissitedoesnotexist for more details",
                        5,
                        10,
                        Timestamp.now(),
                        "Malick")))

    val event = eventList.events.first()
    // fullevent = event.copy(participants = 10, maxParticipants = 10)

    // Park with events
    val park =
        Park(
            pid = "123",
            name = "Sample Park",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 2,
            capacity = 10,
            occupancy = 5,
            events = emptyList())

    parkViewModel.setCurrentPark(park)
    eventViewModel.setCurrentEvent(event)

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
          ProgressionViewModel(mockk<ProgressionRepositoryFirestore>()))
          userViewModel,
          parkViewModel,
          eventViewModel)
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
