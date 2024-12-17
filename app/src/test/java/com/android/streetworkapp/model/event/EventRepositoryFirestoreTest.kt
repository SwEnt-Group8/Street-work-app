package com.android.streetworkapp.model.event

import androidx.test.core.app.ApplicationProvider
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.timeout
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EventRepositoryFirestoreTest {

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var collection: CollectionReference
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var document: DocumentSnapshot
  @Mock private lateinit var query: QuerySnapshot

  private lateinit var eventRepository: EventRepositoryFirestore
  private lateinit var event: Event

  @Before
  fun setUp() {

    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    eventRepository = EventRepositoryFirestore(db)
    event =
        Event(
            eid = "1",
            title = "Group workout",
            description = "A fun group workout session to train new skills",
            participants = 3,
            maxParticipants = 5,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "user123")

    whenever(db.collection("events")).thenReturn(collection)
    `when`(db.collection(any())).thenReturn(collection)
    `when`(collection.document(any())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))

    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn(event.eid)
    `when`(document.get("date")).thenReturn(event.date)
    `when`(document.get("title")).thenReturn(event.title)
    `when`(document.get("owner")).thenReturn(event.owner)
    `when`(document.get("participants")).thenReturn(event.participants.toLong())
    `when`(document.get("description")).thenReturn(event.description)
    `when`(document.get("capacity")).thenReturn(event.listParticipants)
    `when`(document.get("maxParticipants")).thenReturn(event.maxParticipants.toLong())
    `when`(document.get("parkId")).thenReturn(event.parkId)
    `when`(document.get("listParticipants")).thenReturn(event.listParticipants)
    `when`(document.get("status")).thenReturn("CREATED")
  }

  @Test
  fun getNewEidReturnsUniqueId() {

    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueEventId")

    val eid = eventRepository.getNewEid()
    assertEquals("uniqueEventId", eid)
  }

  @Test
  fun addEventAddsEventSuccessfully() = runTest {
    `when`(documentRef.set(event.eid)).thenReturn(Tasks.forResult(null))

    eventRepository.addEvent(event)
    verify(documentRef).set(event)
  }

  @Test
  fun getEventByEid_calls_getdocument() = runTest {
    eventRepository.getEventByEid(event.eid)

    verify(documentRef, timeout(1000)).get()
  }

  @Test
  fun getEvents_calls_getDocument() = runTest {
    val park =
        Park(
            pid = "123",
            name = "EPFL Esplanade",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 102,
            capacity = 10,
            occupancy = 8,
            events = listOf(event.eid))
    whenever(db.collection("parks")).thenReturn(collection)

    eventRepository.getEvents(park, {}, { throw it })

    verify(documentRef, timeout(1000)).get()
  }

  @Test
  fun addParticipantToEvent_calls_update() = runTest {
    `when`(documentRef.update(eq("participants"), any(), eq("listParticipants"), any()))
        .thenReturn(Tasks.forResult(null))

    eventRepository.addParticipantToEvent(event.eid, "123")

    verify(documentRef, timeout(1000))
        .update(eq("participants"), any(), eq("listParticipants"), any())
  }

  @Test
  fun removeParticipantFromEvent_calls_update() = runTest {
    `when`(documentRef.update(eq("participants"), any(), eq("listParticipants"), any()))
        .thenReturn(Tasks.forResult(null))

    eventRepository.removeParticipantFromEvent(event.eid, "123")

    verify(documentRef, timeout(1000))
        .update(eq("participants"), any(), eq("listParticipants"), any())
  }

  @Test
  fun documentToEvent_works() {
    val fetchedEvent = eventRepository.documentToEvent(document)
    assertEquals(event, fetchedEvent)
  }

  @Test
  fun updateStatus_calls_update() = runTest {
    `when`(documentRef.update(eq("status"), any())).thenReturn(Tasks.forResult(null))

    eventRepository.updateStatus(event.eid, EventStatus.ENDED)

    verify(documentRef, timeout(1000)).update(eq("status"), any())
  }

  @Test
  fun deleteEvent_calls_delete() = runTest {
    `when`(documentRef.delete()).thenReturn(Tasks.forResult(null))

    eventRepository.deleteEvent(event)

    verify(documentRef, timeout(1000)).delete()
  }

  @Test
  fun removeParticipantFromAllEventsRemovesParticipantAndDeletesEvents() = runTest {
    // Mock the collection
    whenever(db.collection("events")).thenReturn(collection)

    // Mock the query returned by whereArrayContains
    val query = mock<QuerySnapshot>()
    whenever(collection.whereArrayContains("listParticipants", "user123")).thenReturn(mock())
    whenever(collection.whereArrayContains("listParticipants", "user123").get())
        .thenReturn(Tasks.forResult(query))

    // Mock documents in the query snapshot
    whenever(query.documents).thenReturn(listOf(document))

    // Mock event details for owned event
    whenever(document.exists()).thenReturn(true)
    whenever(document.id).thenReturn(event.eid)
    whenever(document.get("owner")).thenReturn("user123")
    whenever(document.get("listParticipants")).thenReturn(listOf("user123"))

    // Mock Firestore delete and update operations
    whenever(documentRef.delete()).thenReturn(Tasks.forResult(null))
    whenever(documentRef.update(eq("participants"), any(), eq("listParticipants"), any()))
        .thenReturn(Tasks.forResult(null))

    // Call the method under test
    eventRepository.removeParticipantFromAllEvents("user123")

    // Verify delete is called for the owned event
    verify(documentRef, timeout(1000)).delete()

    // Verify update is not called (since the event is owned and deleted)
    verify(documentRef, timeout(1000).times(0))
        .update(eq("participants"), any(), eq("listParticipants"), any())

    // Mock event details for non-owned event
    whenever(document.get("owner")).thenReturn("user456")
    whenever(document.get("listParticipants")).thenReturn(listOf("user123", "user456"))

    // Call the method under test again
    eventRepository.removeParticipantFromAllEvents("user123")

    // Verify update is called for the non-owned event
    verify(documentRef, timeout(1000))
        .update(eq("participants"), any(), eq("listParticipants"), any())
  }
}
