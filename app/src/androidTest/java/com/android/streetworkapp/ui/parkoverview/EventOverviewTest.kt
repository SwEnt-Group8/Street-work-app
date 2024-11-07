package com.android.streetworkapp.ui.parkoverview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.ui.event.EventOverviewScreen
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.utils.toFormattedString
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class EventOverviewTest {
  private lateinit var park: Park
  private lateinit var navigationActions: NavigationActions
  private lateinit var event: Event

  // cannot be tested right now
  // private lateinit var fullevent: Event

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
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
                        "Malick")))

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
  }

  @Test
  fun everyImmutableComposableAreDisplayed() {

    composeTestRule.setContent { EventOverviewScreen(navigationActions, event, park) }

    composeTestRule.onNodeWithTag("eventOverviewScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ownerIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventOwner").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dateIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("date").assertIsDisplayed()
    composeTestRule.onNodeWithTag("participantsIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("participants").assertIsDisplayed()
    composeTestRule.onNodeWithTag("locationIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("location").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("eventImage").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventOwner").assertTextEquals("Organized by: ${event.owner}")
    composeTestRule.onNodeWithTag("date").assertTextEquals(event.date.toFormattedString())
    composeTestRule
        .onNodeWithTag("participants")
        .assertTextEquals("Participants: ${event.participants}/${event.maxParticipants}")
    composeTestRule.onNodeWithTag("location").assertTextEquals("at ${park.name}")
  }

  @Test
  fun everythingIsDisplayedInDashBoard() {

    composeTestRule.setContent { EventOverviewScreen(navigationActions, event, park) }

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
}
