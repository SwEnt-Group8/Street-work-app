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
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.ui.park.ParkOverviewScreen
import com.google.firebase.Timestamp
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParkOverviewTest {
  private lateinit var noEventPark: Park
  private lateinit var park: Park
  private lateinit var invalidRatingPark: Park
  private lateinit var invalidOccupancyPark: Park

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    val emptyEventList = EventList(events = emptyList())
    val eventList =
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

    // Park without events
    noEventPark =
        Park(
            pid = "1",
            name = "EPFL Esplanade",
            location = "EPFL",
            image = null,
            rating = 4.5f,
            nbrRating = 102,
            occupancy = 0.8f,
            events = emptyEventList)

    // Park with events
    park =
        Park(
            pid = "2",
            name = "EPFL Esplanade",
            location = "EPFL",
            image = null,
            rating = 4.5f,
            nbrRating = 102,
            occupancy = 0.8f,
            events = eventList)

    // Park with invalid rating
    invalidRatingPark =
        Park(
            pid = "3",
            name = "EPFL Esplanade",
            location = "EPFL",
            image = null,
            rating = 6.0f,
            nbrRating = 102,
            occupancy = 0.8f,
            events = eventList)

    // Park with invalid occupancy
    invalidOccupancyPark =
        Park(
            pid = "4",
            name = "EPFL Esplanade",
            location = "EPFL",
            image = null,
            rating = 4.5f,
            nbrRating = 102,
            occupancy = 1.2f,
            events = eventList)
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.setContent { ParkOverviewScreen(park) }
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
  fun displayCorrectParkDetails() {
    composeTestRule.setContent { ParkOverviewScreen(park) }
    composeTestRule.onNodeWithTag("title").assertTextEquals("EPFL Esplanade")
    composeTestRule.onNodeWithTag("nbrReview").assertTextEquals("(102)")
    composeTestRule.onNodeWithTag("occupancyText").assertTextEquals("80% Occupancy")
  }

  @Test
  fun displayCorrectEvent() {
    composeTestRule.setContent { ParkOverviewScreen(park) }
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
  }

  @Test
  fun displayTextWhenNoEvent() {
    composeTestRule.setContent { ParkOverviewScreen(noEventPark) }
    composeTestRule.onNodeWithTag("noEventText").isDisplayed()
    composeTestRule.onNodeWithTag("noEventText").assertTextEquals("No events is planned yet")
  }

  @Test
  fun buttonAreClickable() {
    composeTestRule.setContent { ParkOverviewScreen(park) }
    composeTestRule.onNodeWithTag("eventButton").performClick()
    composeTestRule.onNodeWithTag("createEventButton").performClick()
  }

  @Test
  fun invalidRatingTriggersException() {
    assertThrows(IllegalArgumentException::class.java) {
      composeTestRule.setContent { ParkOverviewScreen(invalidRatingPark) }
    }
  }

  @Test
  fun invalidOccupancyTriggersException() {
    assertThrows(IllegalArgumentException::class.java) {
      composeTestRule.setContent { ParkOverviewScreen(invalidOccupancyPark) }
    }
  }
}
