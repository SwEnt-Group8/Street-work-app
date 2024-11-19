package com.android.streetworkapp.event

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.event.EventOverviewScreen
import com.android.streetworkapp.ui.navigation.LIST_OF_SCREENS
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.ScreenParams
import com.android.streetworkapp.utils.toFormattedString
import com.google.firebase.Timestamp
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class EventOverviewTest {
  private lateinit var park: Park
  private lateinit var owner: User
  private lateinit var joiner: User
  private lateinit var navigationActions: NavigationActions
  private lateinit var event: Event
  private lateinit var eventRepository: EventRepository
  private lateinit var eventViewModel: EventViewModel
  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var screenParams: ScreenParams

  // cannot be tested right now
  // private lateinit var fullevent: Event

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    eventRepository = mock(EventRepository::class.java)
    eventViewModel = EventViewModel(eventRepository)
    parkRepository = mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)
    screenParams = LIST_OF_SCREENS.last()

    navigationActions = mock(NavigationActions::class.java)
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
                        "123",
                        listOf("123"))))

    event = eventList.events.first()
    // fullevent = event.copy(participants = 10, maxParticipants = 10)

    // Park with events
    park =
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
    owner = User(event.owner, "test", "test", 0, listOf(), "test")
    joiner = owner.copy(uid = "joiner")
  }

  @Test
  fun everyImmutableComposableAreDisplayed() = runTest {
    eventViewModel.setCurrentEvent(event)
    parkViewModel.setCurrentPark(park)
    composeTestRule.setContent {
      EventOverviewScreen(eventViewModel, parkViewModel, userViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("eventOverviewScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ownerIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventOwner").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dateIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("participantsIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("participants").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("location").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventOwner").assertTextEquals("Organized by: ${event.owner}")
    composeTestRule.onNodeWithTag("date").assertTextEquals(event.date.toFormattedString())
    composeTestRule
        .onNodeWithTag("participants")
        .assertTextEquals("Participants: ${event.participants}/${event.maxParticipants}")
    composeTestRule.onNodeWithTag("location").assertTextEquals("at ${park.name}")
  }

  @Test
  fun everythingIsDisplayedInDashBoard() = runTest {
    eventViewModel.setCurrentEvent(event)
    parkViewModel.setCurrentPark(park)
    composeTestRule.setContent {
      EventOverviewScreen(eventViewModel, parkViewModel, userViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag("eventDashboard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dashboard").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dashboardContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("detailsTab").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventDescription").assertIsDisplayed().performScrollTo()
    composeTestRule.onNodeWithTag("eventDescription").assertTextEquals(event.description)
    composeTestRule.onNodeWithTag("participantsList").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("participantsTab").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("participantsList").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventDescription").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("detailsTab").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("participantsList").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("eventDescription").assertIsDisplayed()
  }

  @Test
  fun joinEventButtonIsDisplayed() = runTest {
    eventViewModel.setCurrentEvent(event)
    parkViewModel.setCurrentPark(park)
    userViewModel.setCurrentUser(joiner)
    composeTestRule.setContent {
      EventOverviewScreen(eventViewModel, parkViewModel, userViewModel, navigationActions)
    }

    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(10000) { composeTestRule.onNodeWithTag("joinEventButton").isDisplayed() }
    composeTestRule.onNodeWithTag("joinEventButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("leaveEventButton").assertIsNotDisplayed()
  }

  @Test
  fun leaveEventButtonIsNotDisplayed() = runTest {
    eventViewModel.setCurrentEvent(event)
    parkViewModel.setCurrentPark(park)
    userViewModel.setCurrentUser(owner)
    composeTestRule.setContent {
      EventOverviewScreen(eventViewModel, parkViewModel, userViewModel, navigationActions)
    }

    composeTestRule.waitForIdle()
    composeTestRule.waitUntil(10000) {
      composeTestRule.onNodeWithTag("leaveEventButton").isDisplayed()
    }
    composeTestRule.onNodeWithTag("joinEventButton").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("leaveEventButton").assertIsDisplayed()
  }
}