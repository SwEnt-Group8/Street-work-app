package com.android.streetworkapp.end2end

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
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

    @get:Rule
    val composeTestRule = createComposeRule()

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

    //mocks
    private lateinit var userRepository: UserRepository

    //Boolean to check if the map is loaded
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

        //Event for this specific end2end test
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
                {mapISLoaded = true},
                userViewModel,
                parkViewModel,
                eventViewModel)
        }
    }

    @Test
    fun e2eCanCreateEventAndDisplayIt() {
        composeTestRule.waitUntil(5000) { mapISLoaded }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

        val uiDevice = UiDevice.getInstance(getInstrumentation())
        val marker = uiDevice.findObject(UiSelector().descriptionContains("Marker"))

        try {
            marker.click()
        } catch (e: UiObjectNotFoundException) {
            e.printStackTrace()
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

        composeTestRule.onNodeWithTag("EventButton ${testEvent.title}").assertIsDisplayed().performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("EventOverviewScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("eventTitle").assertIsDisplayed().assertTextContains(testEvent.title)

        composeTestRule.onNodeWithTag("eventOwner").assertIsDisplayed().assertTextContains("Organized by: ${testEvent.owner}")

        composeTestRule.onNodeWithTag("date").assertIsDisplayed()

        composeTestRule.onNodeWithTag("eventDescription").assertIsDisplayed().assertTextContains(testEvent.description)

        composeTestRule.onNodeWithTag("participants").assertIsDisplayed().assertTextContains("Participants: ${testEvent.participants}/${testEvent.maxParticipants}")

        composeTestRule.onNodeWithTag("participantsTab").assertIsDisplayed().performClick()

        composeTestRule.onNodeWithTag("participantsList").assertIsDisplayed()
    }
}