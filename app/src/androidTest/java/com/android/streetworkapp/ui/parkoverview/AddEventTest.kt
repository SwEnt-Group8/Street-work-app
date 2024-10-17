package com.android.streetworkapp.ui.parkoverview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.park.AddEventScreen
import com.android.streetworkapp.ui.park.EventDescriptionSelection
import com.android.streetworkapp.ui.park.EventTitleSelection
import com.android.streetworkapp.ui.park.ParticipantNumberSelection
import com.android.streetworkapp.ui.park.TimeSelection
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class AddEventTest {

  private lateinit var event: Event
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    event =
        Event(
            eid = "1",
            title = "Group workout",
            description = "A fun group workout session to train new skills",
            participants = 3,
            maxParticipants = 5,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "user123")
  }

  @Test
  fun participantNumberSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    assert(eventCopy.maxParticipants == 5)
    composeTestRule.setContent { ParticipantNumberSelection(eventCopy) }
    // Deprecated "performGesture" is used, because "performTouchInput" does not seem to work with
    // the slider
    composeTestRule.onNodeWithTag("sliderMaxParticipants").performGesture { this.swipeRight() }
    assert(eventCopy.maxParticipants == 10)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun timeSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    assert(eventCopy.date == Timestamp(0, 0))
    composeTestRule.setContent { TimeSelection(eventCopy) }
    composeTestRule.onNodeWithTag("dateIcon").performClick()
    composeTestRule.onNodeWithTag("validateDate").performClick()
    composeTestRule.onNodeWithTag("timeIcon").performClick()
    composeTestRule.onNodeWithTag("validateTime").performClick()
    assert(eventCopy.date != Timestamp(0, 0))
  }

  @Test
  fun titleSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    composeTestRule.setContent { EventTitleSelection(eventCopy) }
    composeTestRule.onNodeWithTag("titleTag").performTextClearance()
    composeTestRule.onNodeWithTag("titleTag").performTextInput("test")

    assert(eventCopy.title == "test")
  }

  @Test
  fun descriptionSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    composeTestRule.setContent { EventDescriptionSelection(eventCopy) }
    composeTestRule.onNodeWithTag("descriptionTag").performTextClearance()
    composeTestRule.onNodeWithTag("descriptionTag").performTextInput("test")

    assert(eventCopy.description == "test")
  }

  @Test
  fun addEventScreenIsDisplayed() {
    composeTestRule.setContent { AddEventScreen(navigationActions) }
    composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addEventButton").assertIsDisplayed()
  }
}