package com.android.streetworkapp.end2end

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
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
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.NominatimParkNameRepository
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class End2EndCreateEvent {

  @get:Rule val composeTestRule = createComposeRule()

  // repositories and viewmodels tested in this end2end test
  private lateinit var firestoreDB: FirebaseFirestore
  private lateinit var parkLocationRepository: OverpassParkLocationRepository
  private lateinit var parkNameRepository: NominatimParkNameRepository
  private lateinit var parkRepository: ParkRepository
  private lateinit var eventRepository: EventRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventViewModel: EventViewModel
  private lateinit var progressionViewModel: ProgressionViewModel

  // mock event
  private lateinit var testEvent: Event

  // mocked repository not used in this test
  private lateinit var userRepository: UserRepository
  private lateinit var progressionRepository: ProgressionRepository

  // Boolean to check if the map is loaded
  private var mapISLoaded = false

  // Define a state variable to hold the current screen
  private var currentScreen by mutableStateOf(Screen.MAP)

  @Before
  fun setUp() {

    // Instantiate fire store database and associated user repository :
    firestoreDB = FirebaseFirestore.getInstance()

    // delete all data in the testParks collection for consistency
    val eventsCollection = firestoreDB.collection("testParks")

    eventsCollection
        .get()
        .addOnSuccessListener { querySnapshot ->
          for (document in querySnapshot.documents) {
            document.reference
                .delete()
                .addOnSuccessListener { println("Event ${document.id} deleted successfully") }
                .addOnFailureListener { e ->
                  println("Error deleting event ${document.id}: ${e.message}")
                }
          }
        }
        .addOnFailureListener { e -> println("Error retrieving events: ${e.message}") }

    // repositories
    parkLocationRepository = OverpassParkLocationRepository(OkHttpClient())
    parkNameRepository = NominatimParkNameRepository(OkHttpClient())
    eventRepository = EventRepositoryFirestore(firestoreDB)
    userRepository = mock(UserRepository::class.java)
    parkRepository = ParkRepositoryFirestore(firestoreDB, testing = true)
    progressionRepository = mock(ProgressionRepository::class.java)

    // viewmodels
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    userViewModel = UserViewModel(userRepository)
    parkViewModel = ParkViewModel(parkRepository, parkNameRepository)
    eventViewModel = EventViewModel(eventRepository)
    progressionViewModel = ProgressionViewModel(progressionRepository)

    testEvent =
        Event(
            eid = "13413555968",
            title = "E2ECreateEvent",
            description = "This event aims to test the creation of an event",
            participants = 1,
            maxParticipants = 2,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "E2ECreateEventOwner")

    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(currentScreen) },
          { mapISLoaded = true },
          userViewModel,
          parkViewModel,
          eventViewModel,
          progressionViewModel,
          mock(WorkoutViewModel::class.java),
          mock(TextModerationViewModel::class.java),
          mock(ImageViewModel::class.java),
          true)
    }
  }

  /**
   * This end to end test simulates a user flow where the user creates an event and then displays it
   * in the park overview screen. We also verify that the event is properly displayed in the event
   * overview screen. Warning: this test fails between 00:00 and 01:00 UTC time because of the date
   * comparison
   */
  @Ignore("Test does not work on CI but should run locally")
  @Test
  fun e2eCanCreateEventAndDisplayIt() {
    // Wait for the map to be loaded
    composeTestRule.waitUntil(100000) { mapISLoaded }
    composeTestRule.waitForIdle()

    // test the map screen
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

    // find a marker on the map and click it
    val uiDevice = UiDevice.getInstance(getInstrumentation())

    composeTestRule.waitUntil {
      uiDevice.findObject(UiSelector().descriptionContains("Marker1")).exists()
    }

    val marker = uiDevice.findObject(UiSelector().descriptionContains("Marker1"))

    try {
      marker.click()
    } catch (e: UiObjectNotFoundException) {
      throw e
    }

    // UiDevice does not seem to navigate properly to the next screen, so we manually set the
    // current screen to Park Overview
    currentScreen = Screen.PARK_OVERVIEW

    composeTestRule.waitUntil(
        10000) { // this value is arbitrary, we just don't want the test to completely halt. Might
          // need to tune it for the CI
          composeTestRule.onNodeWithTag("parkOverviewScreen").isDisplayed()
        }

    composeTestRule.onNodeWithTag("parkOverviewScreen").assertIsDisplayed()

    // create an event
    composeTestRule.onNodeWithTag("createEventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    // I'm for real this does not work between 00:00 and 01:00 UTC time
    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("titleTag").assertIsDisplayed().performTextClearance()
    composeTestRule.onNodeWithTag("titleTag").performTextInput(testEvent.title)

    composeTestRule.onNodeWithTag("descriptionTag").assertIsDisplayed().performTextClearance()
    composeTestRule.onNodeWithTag("descriptionTag").performTextInput(testEvent.description)

    composeTestRule.onNodeWithTag("dateIcon").performClick()
    composeTestRule.onNodeWithTag("validateDate").performClick()
    composeTestRule.onNodeWithTag("timeIcon").performClick()
    composeTestRule.onNodeWithTag("validateTime").performClick()

    composeTestRule.onNodeWithTag("addEventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    // verify that the event is properly displayed on the park overview screen
    composeTestRule.onNodeWithTag("parkOverviewScreen").assertIsDisplayed()

    composeTestRule.waitUntil { composeTestRule.onNodeWithTag("eventButton").isDisplayed() }

    composeTestRule.onNodeWithText(testEvent.title).assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    // navigate to the event overview screen and verify that the event is properly displayed
    composeTestRule.onNodeWithTag("eventOverviewScreen").assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("eventTitle")
        .assertIsDisplayed()
        .assertTextContains(testEvent.title)

    composeTestRule.onNodeWithTag("eventOwner").assertIsDisplayed()

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
