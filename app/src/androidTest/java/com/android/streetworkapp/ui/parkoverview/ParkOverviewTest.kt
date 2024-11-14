package com.android.streetworkapp.ui.parkoverview

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.park.ParkOverviewScreen
import com.android.streetworkapp.ui.park.RatingComponent
import com.google.firebase.Timestamp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ParkOverviewTest {

  private lateinit var noEventPark: Park
  private lateinit var park: Park
  private lateinit var invalidRatingPark: Park
  private lateinit var invalidOccupancyPark: Park
  private lateinit var eventViewModel: EventViewModel
  private lateinit var parkViewModel: ParkViewModel

  private val eventList =
      EventList(
          events =
              listOf(
                  Event(
                      eid = "1",
                      title = "Group workout",
                      description = "A fun group workout session to train new skills",
                      participants = 3,
                      maxParticipants = 5,
                      date = Timestamp(0, 0), // 01/01/1970 00:00
                      owner = "user123")))

  // Mocks
  private lateinit var navigationActions: NavigationActions
  private lateinit var eventRepository: EventRepository
  private lateinit var parkRepository: ParkRepository
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    eventRepository = mock(EventRepository::class.java)
    eventViewModel = EventViewModel(eventRepository)
    parkRepository = mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)

    `when`(navigationActions.currentRoute()).thenReturn(Route.MAP)

    // Park with events
    park =
        Park(
            pid = "123",
            name = "EPFL Esplanade",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 102,
            capacity = 10,
            occupancy = 8,
            events = listOf("event1", "event2"))

    // Park with no events
    noEventPark = park.copy(events = emptyList())

    // Park with invalid rating
    invalidRatingPark = park.copy(rating = 6.0f)

    // Park with invalid occupancy
    invalidOccupancyPark = park.copy(occupancy = -1)
    invalidOccupancyPark = park.copy(capacity = 2)
  }

  @Test
  fun parkOverviewScreenHasRequiredComponents() {
    parkViewModel.setCurrentPark(park)
    composeTestRule.setContent {
      ParkOverviewScreen(
          parkViewModel, eventViewModel = eventViewModel, navigationActions = navigationActions)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("parkOverviewScreen").isDisplayed()
    composeTestRule.onNodeWithTag("imageTitle").isDisplayed()
    composeTestRule.onNodeWithTag("title").isDisplayed()
    composeTestRule.onNodeWithTag("parkDetails").isDisplayed()
    composeTestRule.onNodeWithTag("ratingComponent").isDisplayed()
    composeTestRule.onNodeWithTag("nbrReview").isDisplayed()
    composeTestRule.onNodeWithTag("occupancyBar").isDisplayed()
    composeTestRule.onNodeWithTag("occupancyText").isDisplayed()
    composeTestRule.onNodeWithTag("eventItemList").isDisplayed()
    composeTestRule.onNodeWithTag("eventItem").isDisplayed()
    composeTestRule.onNodeWithTag("participantsText").isDisplayed()
    composeTestRule.onNodeWithTag("dateText").isDisplayed()
    composeTestRule.onNodeWithTag("profileIcon").isDisplayed()
    composeTestRule.onNodeWithTag("eventButton").isDisplayed()
    composeTestRule.onNodeWithTag("eventButtonText").isDisplayed()
    composeTestRule.onNodeWithTag("createEventButton").isDisplayed()
  }

  @Test
  fun parkOverviewScreenDisplaysCorrectEvent() = runTest {
    `when`(eventRepository.getEvents(any(), any(), any())).then {
      it.getArgument<(List<Event>) -> Unit>(1)(listOf(eventList.events.first()))
    }
    parkViewModel.setCurrentPark(park)

    composeTestRule.setContent {
      ParkOverviewScreen(
          parkViewModel, eventViewModel = eventViewModel, navigationActions = navigationActions)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("createEventButton").assertTextEquals("Create an event")
    composeTestRule.onNodeWithTag("eventItem").assertTextContains("Group workout")
    composeTestRule
        .onNodeWithTag("participantsText", useUnmergedTree = true)
        .assertTextContains("Participants 3/5")
    composeTestRule
        .onNodeWithTag("dateText", useUnmergedTree = true)
        .assertTextContains("01/01/1970 00:00")
    composeTestRule
        .onNodeWithTag("eventButtonText", useUnmergedTree = true)
        .assertTextContains("About")
    composeTestRule.onNodeWithTag("eventButton").performClick()

    assertEquals(eventList.events.first(), eventViewModel.currentEvent.value)

    verify(navigationActions).navigateTo(Screen.EVENT_OVERVIEW)
  }

  @Test
  fun parkOverviewScreenDisplaysTextWhenNoEvent() {
    parkViewModel.setCurrentPark(noEventPark)
    composeTestRule.setContent {
      ParkOverviewScreen(
          parkViewModel, eventViewModel = eventViewModel, navigationActions = navigationActions)
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("noEventText").isDisplayed()
    composeTestRule.onNodeWithTag("noEventText").assertTextEquals("No event is planned yet")
  }

  /*
    // TODO: Adapt this test to the new event data class
  @Test
  fun parkOverviewScreenButtonsAreClickable() {
    composeTestRule.setContent { ParkOverviewScreen(park) }
    composeTestRule.onNodeWithTag("eventButton").performClick()
    composeTestRule.onNodeWithTag("createEventButton").performClick()
  }
  */

  @Test
  fun parkOverviewScreenInvalidRatingTriggersException() {
    parkViewModel.setCurrentPark(invalidRatingPark)
    assertThrows(IllegalArgumentException::class.java) {
      composeTestRule.setContent {
        ParkOverviewScreen(
            parkViewModel, eventViewModel = eventViewModel, navigationActions = navigationActions)
      }
    }
  }

  @Test
  fun ratingComponentWithMinRating() {
    composeTestRule.setContent { RatingComponent(rating = 1, nbrReview = 10) }
    composeTestRule.onNodeWithTag("ratingComponent").isDisplayed()
    composeTestRule.onNodeWithTag("nbrReview").assertTextEquals("(10)")
  }

  @Test
  fun ratingComponentWithMaxRating() {
    composeTestRule.setContent { RatingComponent(rating = 5, nbrReview = 20) }
    composeTestRule.onNodeWithTag("ratingComponent").isDisplayed()
    composeTestRule.onNodeWithTag("nbrReview").assertTextEquals("(20)")
  }
}
