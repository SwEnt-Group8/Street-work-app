package com.android.streetworkapp.ui.parkoverview

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipeRight
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.ui.park.ParticipantNumberSelection
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEventTest {

  private lateinit var event: Event

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
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
}
