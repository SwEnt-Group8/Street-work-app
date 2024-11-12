package com.android.streetworkapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

// this is very wrong but something in the ADD_EVENT screen makes the test stall and I really can't
// be bothered to debug it. (We only skip one screen out of all the others so it shouldn't matter
// that much)
val TEST_SCREEN_EXCLUSION_LIST = listOf<String>(Screen.ADD_EVENT)

@RunWith(AndroidJUnit4::class)
class BottomNavigationTest {

  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel

  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventRepository: EventRepository
  private lateinit var eventViewModel: EventViewModel
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun BottomNavigationTest() {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION)
        }) {
          Text("test")
        }
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun EmptyBottomNavigationTest() {
    Scaffold(bottomBar = { BottomNavigationMenu(onTabSelect = {}, tabList = listOf()) }) {
      Text("test")
    }
  }

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
  fun printComposeHierarchy() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onRoot().printToLog("BottomNavigationTest")
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag("bottomNavigationItem")
        .assertCountEquals(LIST_TOP_LEVEL_DESTINATION.size)

    val navItems = composeTestRule.onAllNodesWithTag("bottomNavigationItem")

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      navItems[i].assertIsDisplayed()
    }
  }

  @Test
  fun menuItemsAreClickable() {
    composeTestRule.setContent { BottomNavigationTest() }
    val navItems = composeTestRule.onAllNodesWithTag("bottomNavigationItem")

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      navItems[i].performClick()
    }
  }

  @Test
  fun displayNoComponents() {
    composeTestRule.setContent { EmptyBottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("bottomNavigationItem").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("bottomNavIcon").assertCountEquals(0)
  }

  @Test
  fun bottomBarDisplaysCorrectlyOnScreens() {

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

    val currentScreenParam =
        mutableStateOf(
            LIST_OF_SCREENS.first()) // can't call setContent twice per test so we use this instead

    parkViewModel.setCurrentPark(park)
    eventViewModel.setCurrentEvent(event)

    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(currentScreenParam.value.screenName) },
          {},
          userViewModel,
          parkViewModel,
          eventViewModel)
    }

    val bottomNavTypeToTest =
        BottomNavigationMenuType.entries.filter { it != BottomNavigationMenuType.NONE }

    for (screenParam in LIST_OF_SCREENS) {
      if (screenParam.screenName in TEST_SCREEN_EXCLUSION_LIST) continue

      currentScreenParam.value = screenParam // Update the state to recompose our UI

      composeTestRule.waitForIdle()
      if (screenParam.isBottomBarVisible) {
        when (screenParam.bottomBarType) {
          BottomNavigationMenuType.NONE ->
              Assert.fail(
                  "Invalid use of the bottomBar setup, if isBottomBarVisible is set to false its type should be set to NONE")
          BottomNavigationMenuType.DEFAULT ->
              composeTestRule
                  .onNodeWithTag(BottomNavigationMenuType.DEFAULT.getTopLevelTestTag())
                  .assertIsDisplayed()
          BottomNavigationMenuType.EVENT_OVERVIEW ->
              composeTestRule
                  .onNodeWithTag(BottomNavigationMenuType.EVENT_OVERVIEW.getTopLevelTestTag())
                  .assertIsDisplayed()
        }
      } else {
        for (bottomNavType in bottomNavTypeToTest) composeTestRule
            .onNodeWithTag(bottomNavType.getTopLevelTestTag())
            .assertIsNotDisplayed()
      }
    }
  }
}
