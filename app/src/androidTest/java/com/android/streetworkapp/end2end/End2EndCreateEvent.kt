package com.android.streetworkapp.end2end

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.isDisplayed
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
import androidx.test.rule.GrantPermissionRule
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.moderation.TextEvaluation
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkNameRepository
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class End2EndCreateEvent {

  @get:Rule val composeTestRule = createComposeRule()

  // grant the permission to access location (remove the window for permission)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  // Mocked database
  @Mock private lateinit var firestoreDB: FirebaseFirestore

  @Mock private lateinit var eventCollection: CollectionReference
  @Mock private lateinit var eventDocumentRef: DocumentReference
  @Mock private lateinit var eventDocument: DocumentSnapshot
  @Mock private lateinit var parkQuery: Query
  @Mock private lateinit var parkQuerySnapshot: QuerySnapshot

  @Mock private lateinit var userCollection: CollectionReference
  @Mock private lateinit var userDocumentRef: DocumentReference
  @Mock private lateinit var userDocument: DocumentSnapshot

  @Mock private lateinit var parkCollection: CollectionReference
  @Mock private lateinit var parkDocumentRef: DocumentReference
  @Mock private lateinit var parkDocument: DocumentSnapshot

  // these repositories need to be mocked to avoid network calls as they take an http client as
  // argument
  @Mock private lateinit var parkLocationRepository: ParkLocationRepository
  @Mock private lateinit var parkNameRepository: ParkNameRepository
  @Mock private lateinit var textModerationRepository: TextModerationRepository

  // Used repositories to be tested
  private lateinit var parkRepository: ParkRepository
  private lateinit var eventRepository: EventRepository
  private lateinit var userRepository: UserRepository

  // viewmodels
  private lateinit var parkLocationViewModel: ParkLocationViewModel
  private lateinit var userViewModel: UserViewModel
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var eventViewModel: EventViewModel
  private lateinit var textModerationViewModel: TextModerationViewModel

  // Boolean to check if the map is loaded
  private var mapISLoaded = false

  private var currentUser = User("123", "user123", "", 0, emptyList(), "")

  private var event =
      Event(
          eid = "13413555968",
          title = "E2ECreateEvent",
          description = "This event aims to test the creation of an event",
          participants = 1,
          maxParticipants = 2,
          date = Timestamp(0, 0), // 01/01/1970 00:00
          owner = "E2ECreateEventOwner",
          listParticipants = listOf(currentUser.uid))

  private var park =
      Park(
          "321",
          name = "EPFL Esplanade",
          ParkLocation(46.518659400000004, 6.566561505148001),
          rating = 4f,
          nbrRating = 2,
          capacity = 10,
          occupancy = 5,
          events = listOf(event.eid))

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Define mock behavior for each collection:

    // users:
    `when`(firestoreDB.collection("users")).thenReturn(userCollection)
    `when`(userCollection.document(any())).thenReturn(userDocumentRef)
    `when`(userDocumentRef.get()).thenReturn(Tasks.forResult(userDocument))

    `when`(userDocument.exists()).thenReturn(true)
    `when`(userDocument.id).thenReturn(currentUser.uid)
    `when`(userDocument.get("username")).thenReturn(currentUser.username)
    `when`(userDocument.get("email")).thenReturn(currentUser.email)
    `when`(userDocument.get("score")).thenReturn(currentUser.score)
    `when`(userDocument.get("friends")).thenReturn(currentUser.friends)
    `when`(userDocument.get("picture")).thenReturn(currentUser.picture)

    // parks:
    `when`(firestoreDB.collection("parks")).thenReturn(parkCollection)
    `when`(parkCollection.document(any())).thenReturn(parkDocumentRef)
    `when`(parkCollection.whereEqualTo(eq("location.id"), any())).thenReturn(parkQuery)
    `when`(parkQuery.get()).thenReturn(Tasks.forResult(parkQuerySnapshot))
    `when`(parkQuerySnapshot.documents).thenReturn(listOf(parkDocument))
    `when`(parkDocumentRef.get()).thenReturn(Tasks.forResult(parkDocument))

    `when`(parkDocument.exists()).thenReturn(true)
    `when`(parkDocument.id).thenReturn(park.pid)
    `when`(parkDocument.get("name")).thenReturn(park.name)
    `when`(parkDocument.get("location"))
        .thenReturn(
            mapOf("lat" to park.location.lat, "lon" to park.location.lon, "id" to park.location.id))
    `when`(parkDocument.get("imageReference")).thenReturn(park.imageReference)
    `when`(parkDocument.get("rating")).thenReturn(park.rating.toDouble())
    `when`(parkDocument.get("nbrRating")).thenReturn(park.nbrRating.toLong())
    `when`(parkDocument.get("capacity")).thenReturn(park.capacity.toLong())
    `when`(parkDocument.get("occupancy")).thenReturn(park.occupancy.toLong())
    `when`(parkDocument.get("events")).thenReturn(park.events)

    // events:
    `when`(firestoreDB.collection("events")).thenReturn(eventCollection)
    `when`(eventCollection.document()).thenReturn(eventDocumentRef)
    `when`(eventDocumentRef.id).thenReturn(event.eid)
    `when`(eventCollection.document(any())).thenReturn(eventDocumentRef)
    `when`(eventDocumentRef.get()).thenReturn(Tasks.forResult(eventDocument))
    `when`(eventDocument.exists()).thenReturn(true)
    `when`(eventDocument.id).thenReturn(event.eid)

    `when`(eventDocument.get("date")).thenReturn(event.date)
    `when`(eventDocument.get("title")).thenReturn(event.title)
    `when`(eventDocument.get("owner")).thenReturn(event.owner)
    `when`(eventDocument.get("participants")).thenReturn(event.participants.toLong())
    `when`(eventDocument.get("description")).thenReturn(event.description)
    `when`(eventDocument.get("capacity")).thenReturn(event.listParticipants)
    `when`(eventDocument.get("maxParticipants")).thenReturn(event.maxParticipants.toLong())
    `when`(eventDocument.get("parkId")).thenReturn(event.parkId)
    `when`(eventDocument.get("listParticipants")).thenReturn(event.listParticipants)
    `when`(eventDocument.get("status")).thenReturn("CREATED")

    // repositories
    `when`(parkLocationRepository.search(any(), any(), any(), any())).then {
      it.getArgument<(List<ParkLocation>) -> Unit>(2)(
          listOf(ParkLocation(46.518659400000004, 6.566561505148001, "321")))
    }

    `when`(parkNameRepository.convertLocationIdToParkName(any(), any(), any())).then {
      it.getArgument<(String) -> Unit>(1)("EPFL Esplanade")
    }

    eventRepository = EventRepositoryFirestore(firestoreDB)
    userRepository = UserRepositoryFirestore(firestoreDB)
    parkRepository = ParkRepositoryFirestore(firestoreDB)

    // viewmodels
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    userViewModel = UserViewModel(userRepository)
    parkViewModel = ParkViewModel(parkRepository, parkNameRepository)
    eventViewModel = EventViewModel(eventRepository)
    textModerationViewModel = TextModerationViewModel(textModerationRepository)
  }

  /**
   * First part of the end to end test, simulates login and navigating to the park overview screen
   * by clicking on a marker on the map
   */
  @Ignore
  @Test
  fun e2eLoginAndNavigateToParkOverview() = runTest {
    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(Screen.MAP) },
          { mapISLoaded = true },
          userViewModel,
          parkViewModel,
          eventViewModel,
          mock(ProgressionViewModel::class.java),
          mock(WorkoutViewModel::class.java),
          textModerationViewModel,
          mock(ImageViewModel::class.java),
          mock(PreferencesViewModel::class.java),
          GoogleAuthService(
              "abc",
              mock(FirebaseAuth::class.java, RETURNS_DEFAULTS),
              context = LocalContext.current))
    }

    // Wait for the map to be loaded
    composeTestRule.waitUntil(100000) { mapISLoaded }
    composeTestRule.waitForIdle()

    // test the map screen
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()

    val bounds = composeTestRule.onNodeWithTag("mapScreen").getUnclippedBoundsInRoot()
    val xClickOffset = bounds.left + bounds.size.width / 2
    val yClickOffset = bounds.top + bounds.size.height / 2

    val bottomBarBounds =
        composeTestRule.onNodeWithTag("bottomNavigationMenu").getUnclippedBoundsInRoot()
    val yOffsetCorr = bottomBarBounds.height
    // for some reason the height of the map matches the one of the screen not the actual size
    // it does, this is an ugly fix to correct the position of the click

    composeTestRule.onNodeWithTag("mapScreen").performTouchInput {
      click(Offset(xClickOffset.toPx(), yClickOffset.toPx() - yOffsetCorr.toPx()))
    }

    // need to wait before clicking again else the click is considered as a double click
    composeTestRule.waitUntil(3000) {
      runBlocking { delay(2000) }
      true
    }

    composeTestRule.onNodeWithTag("mapScreen").performTouchInput {
      click(Offset(xClickOffset.toPx(), yClickOffset.toPx() - yOffsetCorr.toPx() - 3))
    }

    composeTestRule.waitUntil(5000) {
      composeTestRule.onNodeWithTag("parkOverviewScreen").isDisplayed()
    }
  }

  /**
   * Second part of the end to end test that simulates a user flow where the user creates an event
   * and then displays it in the park overview screen. We also verify that the event is properly
   * displayed in the event overview screen.
   */
  @Test
  fun e2eCanCreateEventAndDisplayIt() = runTest {
    // This one needs to be initialized here because it evaluateText is a suspend function
    `when`(textModerationRepository.evaluateText(any(), any()))
        .thenReturn(TextEvaluation.Result(true))

    parkViewModel.setPark(park)

    composeTestRule.setContent {
      StreetWorkApp(
          parkLocationViewModel,
          { navigateTo(Screen.PARK_OVERVIEW) },
          { mapISLoaded = true },
          userViewModel,
          parkViewModel,
          eventViewModel,
          mock(ProgressionViewModel::class.java),
          mock(WorkoutViewModel::class.java),
          textModerationViewModel,
          mock(ImageViewModel::class.java),
          mock(PreferencesViewModel::class.java),
          GoogleAuthService(
              "abc",
              mock(FirebaseAuth::class.java, RETURNS_DEFAULTS),
              context = LocalContext.current))
    }

    composeTestRule.waitUntil(5000) {
      composeTestRule.onNodeWithTag("parkOverviewScreen").isDisplayed()
    }

    // create an event
    composeTestRule.onNodeWithTag("createEventButton").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("addEventScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("titleTag").assertIsDisplayed().performTextClearance()

    composeTestRule.onNodeWithTag("titleTag").performTextInput(event.title)

    composeTestRule.onNodeWithTag("descriptionTag").assertIsDisplayed().performTextClearance()

    composeTestRule.onNodeWithTag("descriptionTag").performTextInput(event.description)

    composeTestRule.onNodeWithTag("dateIcon").performClick()

    composeTestRule.onNodeWithTag("validateDate").performClick()

    composeTestRule.onNodeWithTag("timeIcon").performClick()

    composeTestRule.onNodeWithTag("validateTime").performClick()

    composeTestRule.onNodeWithTag("addEventButton").assertIsDisplayed().performClick()

    verify(eventDocumentRef).set(any())

    verify(parkDocumentRef).update(eq("events"), any())

    composeTestRule.waitUntil(5000) { composeTestRule.onNodeWithTag("eventButton").isDisplayed() }

    composeTestRule.onNodeWithText(event.title).assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventButton").assertIsDisplayed().performClick()

    composeTestRule.waitForIdle()

    // navigate to the event overview screen and verify that the event is properly displayed
    composeTestRule.onNodeWithTag("eventOverviewScreen").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventTitle").assertIsDisplayed().assertTextContains(event.title)

    composeTestRule.onNodeWithTag("eventOwner").assertIsDisplayed()

    composeTestRule.onNodeWithTag("date").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventDescription").assertIsDisplayed()

    composeTestRule.onNodeWithTag("participants").assertIsDisplayed()

    composeTestRule.onNodeWithTag("participantsTab").assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag("participantsList").assertIsDisplayed()
  }
}
