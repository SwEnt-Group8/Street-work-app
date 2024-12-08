package com.android.streetworkapp.model.park

// Portions of this code were generated with the help of GitHub Copilot.

import com.android.streetworkapp.model.parklocation.ParkLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class ParkRepositoryFirestoreTest {

  private lateinit var db: FirebaseFirestore
  private lateinit var parkRepository: ParkRepositoryFirestore
  private lateinit var collection: CollectionReference
  private lateinit var documentRef: DocumentReference
  private lateinit var document: DocumentSnapshot

  @Before
  fun setUp() {
    db = mock(FirebaseFirestore::class.java)
    parkRepository = ParkRepositoryFirestore(db)
    collection = mock()
    documentRef = mock()
    document = mock()

    whenever(db.collection("parks")).thenReturn(collection)
  }

  @Test
  fun getNewUidReturnsUniqueId() {
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueId")

    val uid = parkRepository.getNewPid()
    assertEquals("uniqueId", uid)
  }

  @Test
  fun getParkByPidWithValidPidReturnsPark() = runTest {
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document["name"]).thenReturn("Sample Park")
    `when`(document["location"]).thenReturn(ParkLocation(0.0, 0.0, ""))
    `when`(document["imageReference"]).thenReturn("parks/sample.png")
    `when`(document["rating"]).thenReturn(4.0)
    `when`(document["nbrRating"]).thenReturn(2L)
    `when`(document["capacity"]).thenReturn(10L)
    `when`(document["occupancy"]).thenReturn(5L)
    `when`(document["events"]).thenReturn(listOf("event1", "event2"))

    // Mock Firestore interactions
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)

    // Use TaskCompletionSource to create a controllable Task
    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    // Call the repository method
    val park = parkRepository.getParkByPid("123")

    // Assert the result contains expected values
    assertNotNull(park)
    assertEquals("123", park?.pid)
    assertEquals("Sample Park", park?.name)
    assertEquals(ParkLocation(0.0, 0.0, ""), park?.location)
    assertEquals("parks/sample.png", park?.imageReference)
    assertEquals(4.0f, park?.rating)
    assertEquals(2, park?.nbrRating)
    assertEquals(10, park?.capacity)
    assertEquals(5, park?.occupancy)
    assertEquals(listOf("event1", "event2"), park?.events)
  }

  @Test
  fun getParkByPidWithInvalidPidReturnsNull() = runTest {
    `when`(document.exists()).thenReturn(false)
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document("invalidPid")).thenReturn(documentRef)

    // Use TaskCompletionSource to create a controllable Task
    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    // Call the repository method with an invalid PID
    val park = parkRepository.getParkByPid("invalidPid")

    // Assert the result is null
    assertNull(park)
  }

  @Test
  fun getParkByLocationIdWithValidLocationIdReturnsPark() = runTest {
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document["name"]).thenReturn("Sample Park")
    `when`(document["location"]).thenReturn(ParkLocation(0.0, 0.0, "321"))
    `when`(document["imageReference"]).thenReturn("parks/sample.png")
    `when`(document["rating"]).thenReturn(4.0)
    `when`(document["nbrRating"]).thenReturn(2L)
    `when`(document["capacity"]).thenReturn(10L)
    `when`(document["occupancy"]).thenReturn(5L)
    `when`(document["events"]).thenReturn(listOf("event1", "event2"))

    // Mock Firestore interactions
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.whereEqualTo("location.id", "321")).thenReturn(collection)

    // Use TaskCompletionSource to create a controllable Task
    val querySnapshot = mock(QuerySnapshot::class.java)
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    taskCompletionSource.setResult(querySnapshot)
    val task = taskCompletionSource.task
    `when`(collection.get()).thenReturn(task)
    `when`(querySnapshot.documents).thenReturn(listOf(document))

    // Call the repository method
    val park = parkRepository.getParkByLocationId("321")

    // Assert the result contains expected values
    assertNotNull(park)
    assertEquals("123", park?.pid)
    assertEquals("Sample Park", park?.name)
    assertEquals(ParkLocation(0.0, 0.0, ""), park?.location)
    assertEquals("parks/sample.png", park?.imageReference)
    assertEquals(4.0f, park?.rating)
    assertEquals(2, park?.nbrRating)
    assertEquals(10, park?.capacity)
    assertEquals(5, park?.occupancy)
    assertEquals(listOf("event1", "event2"), park?.events)
  }

  @Test
  fun getParkByLocationIdWithInvalidLocationIdReturnsNull() = runTest {
    `when`(document.exists()).thenReturn(false)
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.whereEqualTo("location.id", "invalidLocationId")).thenReturn(collection)

    // Use TaskCompletionSource to create a controllable Task
    val querySnapshot = mock(QuerySnapshot::class.java)
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    taskCompletionSource.setResult(querySnapshot)
    val task = taskCompletionSource.task
    `when`(collection.get()).thenReturn(task)
    `when`(querySnapshot.documents).thenReturn(emptyList())

    // Call the repository method with an invalid location ID
    val park = parkRepository.getParkByLocationId("invalidLocationId")

    // Assert the result is null
    assertNull(park)
  }

  @Test
  fun addParkWithValidParkAddsParkSuccessfully() = runTest {
    val park =
        Park(
            pid = "123",
            name = "Sample Park",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 2,
            capacity = 10,
            occupancy = 5,
            events = listOf("event1", "event2"))

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(park.pid)).thenReturn(documentRef)
    `when`(documentRef.set(park)).thenReturn(Tasks.forResult(null))

    parkRepository.createPark(park)
    verify(documentRef).set(park)
  }

  @Test
  fun updateNameWithValidPidAndNameUpdatesNameSuccessfully() = runTest {
    val pid = "123"
    val name = "Sample Park"

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.update("name", name)).thenReturn(Tasks.forResult(null))

    parkRepository.updateName(pid, name)
    verify(documentRef).update("name", name)
  }

  @Test
  fun updateImageReferenceWithValidPidAndImageReferenceUpdatesImageReferenceSuccessfully() =
      runTest {
        val pid = "123"
        val imageReference = "parks/sample.png"

        `when`(db.collection("parks")).thenReturn(collection)
        `when`(collection.document(pid)).thenReturn(documentRef)
        `when`(documentRef.update("imageReference", imageReference))
            .thenReturn(Tasks.forResult(null))

        parkRepository.updateImageReference(pid, imageReference)
        verify(documentRef).update("imageReference", imageReference)
      }

  @Test
  fun deleteRatingWithValidPidAndRatingDeletesRatingSuccessfully() = runTest {
    val pid = "123"
    val rating = 4
    val currentRating = 4.0
    val currentNbrRating = 3

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))
    `when`(document.getDouble("rating")).thenReturn(currentRating)
    `when`(document.getLong("nbrRating")).thenReturn(currentNbrRating.toLong())

    val newNbrRating = currentNbrRating - 1
    val newRating =
        if (newNbrRating > 0) {
          ((currentRating * currentNbrRating) - rating) / newNbrRating
        } else {
          0.0
        }

    `when`(documentRef.update(mapOf("rating" to newRating, "nbrRating" to newNbrRating)))
        .thenReturn(Tasks.forResult(null))

    parkRepository.deleteRating(pid, rating)
    verify(documentRef).update(mapOf("rating" to newRating, "nbrRating" to newNbrRating))
  }

  @Test
  fun updateCapacityWithValidPidAndCapacityUpdatesCapacitySuccessfully() = runTest {
    val pid = "123"
    val capacity = 10

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.update("capacity", capacity)).thenReturn(Tasks.forResult(null))

    parkRepository.updateCapacity(pid, capacity)
    verify(documentRef).update("capacity", capacity)
  }

  @Test
  fun incrementOccupancyWithValidPidIncrementsOccupancySuccessfully() = runTest {
    val pid = "123"
    val currentOccupancy = 5

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))
    `when`(document.getLong("occupancy")).thenReturn(currentOccupancy.toLong())

    val newOccupancy = currentOccupancy + 1

    `when`(documentRef.update("occupancy", newOccupancy)).thenReturn(Tasks.forResult(null))

    parkRepository.incrementOccupancy(pid)
    verify(documentRef).update("occupancy", newOccupancy)
  }

  @Test
  fun decrementOccupancyWithValidPidDecrementsOccupancySuccessfully() = runTest {
    val pid = "123"
    val currentOccupancy = 5

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))
    `when`(document.getLong("occupancy")).thenReturn(currentOccupancy.toLong())

    val newOccupancy = currentOccupancy - 1

    `when`(documentRef.update("occupancy", newOccupancy)).thenReturn(Tasks.forResult(null))

    parkRepository.decrementOccupancy(pid)
    verify(documentRef).update("occupancy", newOccupancy)
  }

  @Test
  fun addEventToParkWithValidPidAndEidAddsEventToParkSuccessfully() = runTest {
    val pid = "123"
    val eid = "event1"
    val currentEvents = listOf("event2", "event3")

    // Mock Firestore interactions
    whenever(db.collection("parks")).thenReturn(collection)
    whenever(collection.document(pid)).thenReturn(documentRef)
    whenever(documentRef.get()).thenReturn(Tasks.forResult(document))
    whenever(document.get("events")).thenReturn(currentEvents)

    // Call the repository method
    parkRepository.addEventToPark(pid, eid)

    // Verify the interactions
    verify(documentRef).update(eq("events"), any<FieldValue>())
  }

  @Test
  fun deleteEventFromParkWithValidPidAndEidDeletesEventFromParkSuccessfully() = runTest {
    val pid = "123"
    val eid = "event1"
    val currentEvents = listOf("event1", "event2", "event3")

    // Mock Firestore interactions
    whenever(db.collection("parks")).thenReturn(collection)
    whenever(collection.document(pid)).thenReturn(documentRef)
    whenever(documentRef.get()).thenReturn(Tasks.forResult(document))
    whenever(document.get("events")).thenReturn(currentEvents)

    // Call the repository method
    parkRepository.deleteEventFromPark(pid, eid)

    // Verify the interactions
    verify(documentRef).update(eq("events"), any<FieldValue>())
  }

  @Test
  fun deleteParkByPidWithValidPidDeletesParkSuccessfully() = runTest {
    val pid = "123"

    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.delete()).thenReturn(Tasks.forResult(null))

    parkRepository.deleteParkByPid(pid)
    verify(documentRef).delete()
  }

  @Test
  fun getParkByPidWithEmptyPidThrowsIllegalArgumentException() = runTest {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.getParkByPid("") }
    }
  }

  @Test
  fun getParkByLocationIdWithEmptyLocationIdThrowsIllegalArgumentException() = runTest {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.getParkByLocationId("") }
    }
  }

  @Test
  fun createParkWithEmptyPidThrowsIllegalArgumentException() = runTest {
    val park =
        Park(
            pid = "",
            name = "Sample Park",
            location = ParkLocation(0.0, 0.0, "321"),
            imageReference = "parks/sample.png",
            rating = 4.0f,
            nbrRating = 2,
            capacity = 10,
            occupancy = 5,
            events = listOf("event1", "event2"))
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.createPark(park) }
    }
  }

  @Test
  fun updateNameWithEmptyPidThrowsIllegalArgumentException() = runTest {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.updateName("", "Sample Park") }
    }
  }

  @Test
  fun updateImageReferenceWithEmptyPidThrowsIllegalArgumentException() = runTest {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.updateImageReference("", "parks/sample.png") }
    }
  }

  @Test
  fun updateCapacityWithInvalidCapacityThrowsIllegalArgumentException() = runTest {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { parkRepository.updateCapacity("123", 0) }
    }
  }

  @Test
  fun getOrCreateParkByLocationWithExistingLocationReturnsPark() = runTest {
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document["name"]).thenReturn("Sample Park")
    `when`(document["location"]).thenReturn(ParkLocation(0.0, 0.0, "321"))
    `when`(document["imageReference"]).thenReturn("parks/sample.png")
    `when`(document["rating"]).thenReturn(4.0)
    `when`(document["nbrRating"]).thenReturn(2L)
    `when`(document["capacity"]).thenReturn(10L)
    `when`(document["occupancy"]).thenReturn(5L)
    `when`(document["events"]).thenReturn(listOf("event1", "event2"))

    // Mock Firestore interactions
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.whereEqualTo("location.id", "321")).thenReturn(collection)

    // Use TaskCompletionSource to create a controllable Task
    val querySnapshot = mock(QuerySnapshot::class.java)
    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    taskCompletionSource.setResult(querySnapshot)
    val task = taskCompletionSource.task
    `when`(collection.get()).thenReturn(task)
    `when`(querySnapshot.documents).thenReturn(listOf(document))

    // Call the repository method
    val park = parkRepository.getOrCreateParkByLocation(ParkLocation(0.0, 0.0, "321"))

    // Assert the result contains expected values
    assertNotNull(park)
    assertEquals("123", park?.pid)
    assertEquals("Sample Park", park?.name)
    assertEquals(ParkLocation(0.0, 0.0, ""), park?.location)
    assertEquals("parks/sample.png", park?.imageReference)
    assertEquals(4.0f, park?.rating)
    assertEquals(2, park?.nbrRating)
    assertEquals(10, park?.capacity)
    assertEquals(5, park?.occupancy)
    assertEquals(listOf("event1", "event2"), park?.events)
  }

  @Test
  fun addRatingWithNewUserUpdatesRatingSuccessfully() = runTest {
    // Arrange
    val pid = "123"
    val uid = "user_001"
    val newRating = 4.0f
    val initialRating = 3.0f
    val initialNbrRating = 2
    val initialVotersUIDs = listOf("user_002")

    // Set up initial Park data as a DocumentSnapshot mock
    whenever(document.exists()).thenReturn(true)
    whenever(document.toObject(Park::class.java))
        .thenReturn(
            Park(
                pid = pid,
                name = "Sample Park",
                location = ParkLocation(0.0, 0.0, "321"),
                imageReference = "parks/sample.png",
                rating = initialRating,
                nbrRating = initialNbrRating,
                capacity = 10,
                occupancy = 5,
                events = listOf("event1", "event2"),
                votersUIDs = initialVotersUIDs))

    // Mock Firestore interactions
    whenever(db.collection("parks")).thenReturn(collection)
    whenever(collection.document(pid)).thenReturn(documentRef)
    whenever(documentRef.get()).thenReturn(Tasks.forResult(document))

    // Calculate the expected updated values
    val updatedNbrRating = initialNbrRating + 1
    val updatedRating = (newRating + initialRating * initialNbrRating) / updatedNbrRating
    val updatedVotersUIDs = initialVotersUIDs + uid

    // Act
    parkRepository.addRating(pid, uid, newRating)

    // Verify that the Firestore update was called with the correct values
    verify(documentRef)
        .set(
            Park(
                pid = pid,
                name = "Sample Park",
                location = ParkLocation(0.0, 0.0, "321"),
                imageReference = "parks/sample.png",
                rating = updatedRating,
                nbrRating = updatedNbrRating,
                capacity = 10,
                occupancy = 5,
                events = listOf("event1", "event2"),
                votersUIDs = updatedVotersUIDs))
  }

  @Test
  fun deleteRatingWithCurrentNbrRatingOfOneResetsRatingToZero() = runTest {
    // Arrange
    val pid = "123"
    val rating = 4
    val currentRating = 4.0
    val currentNbrRating = 1

    // Mock Firestore interactions
    `when`(db.collection("parks")).thenReturn(collection)
    `when`(collection.document(pid)).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))
    `when`(document.getDouble("rating")).thenReturn(currentRating)
    `when`(document.getLong("nbrRating")).thenReturn(currentNbrRating.toLong())

    // Expected values after removing the rating
    val newNbrRating = 0
    val newRating = 0.0

    // Act
    parkRepository.deleteRating(pid, rating)

    // Assert
    verify(documentRef).update(mapOf("rating" to newRating, "nbrRating" to newNbrRating))
  }

  @Test
  fun addImagesCollectionAddsCorrectCollectionIdToPark() = runTest {
    val mockTask = mock(Task::class.java) as Task<Void>
    whenever(mockTask.isComplete).thenReturn(true)

    val parkId = "parkId"
    val collectionId = "newCollectionId"
    whenever(db.collection(any())).thenReturn(collection)
    whenever(collection.document(parkId)).thenReturn(documentRef)
    whenever(documentRef.update("imagesCollection", collectionId)).thenReturn(mockTask)

    parkRepository.addImagesCollection(parkId, collectionId)
    verify(documentRef).update("imagesCollectionId", collectionId)
  }
}
