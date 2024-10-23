package com.android.streetworkapp.model.event

import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

class EventRepositoryFirestoreTest {

  private lateinit var db: FirebaseFirestore
  private lateinit var eventRepository: EventRepositoryFirestore
  private lateinit var collection: CollectionReference
  private lateinit var documentRef: DocumentReference
  private lateinit var document: DocumentSnapshot

  @Before
  fun setUp() {
    db = mock(FirebaseFirestore::class.java)
    eventRepository = EventRepositoryFirestore(db)
    collection = mock()
    documentRef = mock()
    document = mock()

    whenever(db.collection("events")).thenReturn(collection)
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
    val event =
        Event(
            eid = "1",
            title = "Group workout",
            description = "A fun group workout session to train new skills",
            participants = 3,
            maxParticipants = 5,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "user123")

    `when`(collection.document(event.eid)).thenReturn(documentRef)
    `when`(documentRef.set(event.eid)).thenReturn(Tasks.forResult(null))

    eventRepository.addEvent(event)
    verify(documentRef).set(event)
  }
}
