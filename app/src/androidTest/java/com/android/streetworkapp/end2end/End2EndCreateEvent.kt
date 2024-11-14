package com.android.streetworkapp.end2end

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.size
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.NominatimParkNameRepository
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.Route
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class End2EndCreateEvent {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var firestoreDB: FirebaseFirestore
  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkNameRepository: NominatimParkNameRepository
  private lateinit var parkRepository: ParkRepository
  private lateinit var eventRepository: EventRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventViewModel: EventViewModel

  private lateinit var testEvent: Event

  // mocks
  private lateinit var userRepository: UserRepository

  // Boolean to check if the map is loaded
  private var mapISLoaded = false

  @Before
  fun setUp() {
    // Instantiate fire store database and associated user repository :
    firestoreDB = FirebaseFirestore.getInstance()

    // repositories
    parkLocationRepository = OverpassParkLocationRepository(OkHttpClient())
    parkNameRepository = NominatimParkNameRepository(OkHttpClient())
    eventRepository = EventRepositoryFirestore(firestoreDB)

    userRepository = mock(UserRepository::class.java)

    // viewmodels
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)

    userViewModel = UserViewModel(userRepository)

    // Instantiate park repository :
    parkRepository = ParkRepositoryFirestore(firestoreDB)
    parkViewModel = ParkViewModel(parkRepository, parkNameRepository)

    // Instantiate event viewmodel :
    eventViewModel = EventViewModel(eventRepository)

    // Event for this specific end2end test
    testEvent =
        Event(
            eid = "13413555968",
            title = "E2ECreateEvent",
            description = "This event aims to test the creation of an event",
            participants = 5,
            maxParticipants = 6,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "E2ECreateEventOwner")

    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(Route.MAP) },
          { mapISLoaded = true },
          userViewModel,
          parkViewModel,
          eventViewModel)
    }
  }

  @Test
  fun e2eCanCreateEventAndDisplayIt() {
    composeTestRule.waitUntil(100000) { mapISLoaded }
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

    val bounds = composeTestRule.onNodeWithTag("mapScreen").getUnclippedBoundsInRoot()
    val xClickOffset = bounds.left + bounds.size.width / 2
    val yClickOffset = bounds.top + bounds.size.height / 2

    val bottomBarBounds =
        composeTestRule.onNodeWithTag("bottomNavigationMenu").getUnclippedBoundsInRoot()
    val yOffsetCorr =
        bottomBarBounds
            .height // for some reason the height of the map matches the one of the screen not the
    // actual size it does :)))))))))), this is an ugly fix to correct the position
    // of the click

    composeTestRule.onNodeWithTag("mapScreen").performTouchInput {
      click(Offset(xClickOffset.toPx(), yClickOffset.toPx() - yOffsetCorr.toPx()))
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("ParkOverviewScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("createEventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("titleTag").assertIsDisplayed().performTextClearance()
    composeTestRule.onNodeWithTag("titleTag").performTextInput(testEvent.title)

    composeTestRule.onNodeWithTag("descriptionTag").assertIsDisplayed().performTextClearance()
    composeTestRule.onNodeWithTag("descriptionTag").performTextInput(testEvent.description)

    composeTestRule.onNodeWithTag("dateIcon").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("validateDate").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("timeIcon").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("validateTime").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("addEventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("ParkOverviewScreen").assertIsDisplayed()

    composeTestRule.onNodeWithText(testEvent.title).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("EventButton ${testEvent.title}")
        .assertIsDisplayed()
        .performClick()

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("EventOverviewScreen").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("eventTitle")
        .assertIsDisplayed()
        .assertTextContains(testEvent.title)

    composeTestRule
        .onNodeWithTag("eventOwner")
        .assertIsDisplayed()
        .assertTextContains("Organized by: ${testEvent.owner}")

    composeTestRule.onNodeWithTag("date").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("eventDescription")
        .assertIsDisplayed()
        .assertTextContains(testEvent.description)

    composeTestRule
        .onNodeWithTag("participants")
        .assertIsDisplayed()
        .assertTextContains("Participants: ${testEvent.participants}/${testEvent.maxParticipants}")

    composeTestRule.onNodeWithTag("participantsTab").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("participantsList").assertIsDisplayed()
  }
}
