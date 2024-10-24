package com.android.streetworkapp.model.event

import android.annotation.SuppressLint
import androidx.test.core.app.ApplicationProvider
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
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
    `when`(collection.document()).thenReturn(documentRef)
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
    `when`(collection.document(event.eid)).thenReturn(documentRef)
    `when`(documentRef.set(event.eid)).thenReturn(Tasks.forResult(null))

    eventRepository.addEvent(event)
    verify(documentRef).set(event)
  }

  @SuppressLint("CheckResult")
  @Test
  fun getEvents_calls_collection_get() {
    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(collection.get()).thenReturn(Tasks.forResult(query))


    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(query.documents).thenReturn(listOf())

    val onSuccess: (List<Event>) -> Unit = {}
    val onFailure: (Exception) -> Unit = {}
    eventRepository.getEvents(onSuccess, onFailure)

    // Verify that the 'documents' field was accessed
    org.mockito.kotlin.verify(timeout(100)) { (query).documents }
    verify(collection).get()
  }
}
