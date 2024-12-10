package com.android.streetworkapp.ui.event

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.swipeRight
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AddEventTest {

  private lateinit var event: Event
  private lateinit var navigationActions: NavigationActions
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel

  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel

  private lateinit var eventRepository: EventRepository
  private lateinit var eventViewModel: EventViewModel

  private lateinit var textModerationRepository: TextModerationRepository
  private lateinit var textModerationViewModel: TextModerationViewModel

  lateinit var mockJob: Job

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)

    parkRepository = mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)

    eventRepository = mock(EventRepository::class.java)
    eventViewModel = EventViewModel(eventRepository)

    textModerationRepository = mock(TextModerationRepository::class.java)
    textModerationViewModel = mock(TextModerationViewModel::class.java)

    mockJob = mock(Job::class.java)

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

  @Test
  fun titleSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    composeTestRule.setContent {
      EventTitleSelection(eventCopy, mutableStateOf(false), mutableStateOf(false))
    }
    composeTestRule.onNodeWithTag("titleTag").performTextClearance()
    composeTestRule.onNodeWithTag("titleTag").performTextInput("test")

    assert(eventCopy.title == "test")
  }

  @Test
  fun descriptionSelectionUpdatesEvent() {
    val eventCopy = event.copy()
    composeTestRule.setContent { EventDescriptionSelection(eventCopy, mutableStateOf(false)) }
    composeTestRule.onNodeWithTag("descriptionTag").performTextClearance()
    composeTestRule.onNodeWithTag("descriptionTag").performTextInput("test")

    assert(eventCopy.description == "test")
  }

  @Test
  fun addEventScreenIsDisplayed() {
    `when`(eventRepository.getNewEid()).thenReturn("test")
    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addEventButton").assertIsDisplayed()
  }

  @Test
  fun addEventScreenWithUnchangedTimeDoesNotChangeScreen() {
    `when`(eventRepository.getNewEid()).thenReturn("test")
    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("addEventButton").performClick()
    composeTestRule
        .onNodeWithTag("addEventScreen")
        .assertIsDisplayed() // we want that a click with no changes in the default value does not
    // make the user leave the screen
  }

  @Test
  fun addEventScreenWithUnchangedTitleDoesNotChangeScreen() {
    `when`(eventRepository.getNewEid()).thenReturn("test")
    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("dateIcon").performClick()
    composeTestRule.onNodeWithTag("validateDate").performClick()
    composeTestRule.onNodeWithTag("timeIcon").performClick()
    composeTestRule.onNodeWithTag("validateTime").performClick()

    composeTestRule.onNodeWithTag("addEventButton").performClick()
    composeTestRule
        .onNodeWithTag("addEventScreen")
        .assertIsDisplayed() // we want that a click with changes in the time but no changes in the
    // default value of the title does not make the user leave the screen
  }

  @Test
  fun emptyTitleDisplaysCorrectErrorMessage() {
    whenever(eventRepository.getNewEid()).thenReturn("test")
    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("addEventButton").performClick()
    composeTestRule
        .onNodeWithTag("errorMessage")
        .assertTextEquals(AddEventFormErrorMessages.TITLE_EMPTY_ERROR_MESSAGE)
  }

  @Test
  fun textEvaluationOnEvaluationErrorDisplaysCorrectErrorMessage() {
    whenever(eventRepository.getNewEid()).thenReturn("test")
    whenever(textModerationViewModel.analyzeText(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onErrorCallback = invocation.getArgument<() -> Unit>(2) // Get the onError lambda
      onErrorCallback() // Call it with a mock error
    }
  }

  @Test
  fun textEvaluationIsOverThresholdDisplaysCorrectErrorMessage() {
    whenever(eventRepository.getNewEid()).thenReturn("test")
    whenever(textModerationViewModel.analyzeText(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onTextEvaluationResult =
          invocation.getArgument<(Boolean) -> Unit>(1) // Get the callback lambda
      onTextEvaluationResult(false) // Set to over threshold
    }

    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("titleTag").performTextInput("dummy text over threshold")
    composeTestRule.onNodeWithTag("addEventButton").performClick()
    composeTestRule
        .onNodeWithTag("errorMessage")
        .assertTextEquals(AddEventFormErrorMessages.TEXT_EVALUATION_OVER_THRESHOLDS_ERROR)
  }

  @Test
  fun textEvaluationOverThresholdAndOtherConditionsValidCallCreateEvent() = runTest {
    whenever(eventRepository.getNewEid()).thenReturn("test")
    whenever(eventRepository.addEvent(any())).thenAnswer {
      mockJob
    } // do nothing, we just want to check that it gets called correctly
    whenever(textModerationViewModel.analyzeText(any(), any(), any(), any())).thenAnswer {
        invocation ->
      val onTextEvaluationResult =
          invocation.getArgument<(Boolean) -> Unit>(1) // Get the callback lambda
      onTextEvaluationResult(true) // Set to under thresholds
    }

    composeTestRule.setContent {
      AddEventScreen(
          navigationActions, parkViewModel, eventViewModel, userViewModel, textModerationViewModel)
    }

    composeTestRule.onNodeWithTag("titleTag").performTextInput("dummy title")
    composeTestRule.onNodeWithTag("addEventButton").performClick()
    composeTestRule.onNodeWithTag("errorMessage").assertDoesNotExist()
    verify(eventRepository).addEvent(any())
  }

  @Test
  fun ownerBottomBarIsDisplayed() = runTest {
    `when`(eventRepository.getNewEid()).thenReturn("test")

    val owner = User(event.owner, "owner", "owner", 0, emptyList(), "owner")

    userViewModel.setCurrentUser(owner)

    eventViewModel.setCurrentEvent(event)

    composeTestRule.setContent {
      AddEventScreen(
          navigationActions,
          parkViewModel,
          eventViewModel,
          userViewModel,
          textModerationViewModel,
          editEvent = true)
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("deleteEventButton").assertIsDisplayed().performClick()

    verify(eventRepository).deleteEvent(any())

    verify(parkRepository).deleteEventFromPark(any(), any())
  }
}
