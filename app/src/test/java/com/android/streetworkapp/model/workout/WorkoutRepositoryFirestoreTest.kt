package com.android.streetworkapp.model.workout

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.startsWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WorkoutRepositoryFirestoreTest {

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var collection: CollectionReference
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var document: DocumentSnapshot

  private lateinit var repository: WorkoutRepositoryFirestore

  companion object {
    private const val COLLECTION_PATH = "workoutData"
    private const val WORKOUT_SESSIONS = "workoutSessions"
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    repository = WorkoutRepositoryFirestore(db)

    // Mock Firestore behaviors
    whenever(db.collection(any())).thenReturn(collection)
    whenever(collection.document(any())).thenReturn(documentRef)
    whenever(collection.document()).thenReturn(documentRef)
    whenever(documentRef.id).thenReturn("randomDocumentId")

    // Ensure Firestore operations are mocked to return successful Tasks
    whenever(documentRef.set(any())).thenReturn(Tasks.forResult(null))
    whenever(documentRef.get()).thenReturn(Tasks.forResult(document))
    whenever(documentRef.update(anyString(), any())).thenReturn(Tasks.forResult(null))
    whenever(document.exists()).thenReturn(true)
  }

  @Test
  fun getOrAddWorkoutDataCreatesNewWorkoutDataWhenDocumentDoesNotExist() = runTest {
    // Mock document does not exist
    `when`(document.exists()).thenReturn(false)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))

    val uid = "testUid"
    val expectedWorkoutData = WorkoutData(uid, emptyList())

    val result = repository.getOrAddWorkoutData(uid)

    verify(documentRef).set(expectedWorkoutData)
    assertEquals(expectedWorkoutData, result)
  }

  @Test
  fun getOrAddWorkoutDataReturnsExistingWorkoutData() = runTest {
    // Mock document exists
    `when`(document.exists()).thenReturn(true)
    `when`(document.data).thenReturn(mapOf(WORKOUT_SESSIONS to emptyList<Map<String, Any>>()))
    `when`(document.id).thenReturn("testUid")
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))

    val uid = "testUid"
    val result = repository.getOrAddWorkoutData(uid)

    verify(documentRef, never()).set(any())
    assertEquals(uid, result.userUid)
    assertEquals(emptyList<WorkoutSession>(), result.workoutSessions)
  }

  @Test
  fun addOrUpdateWorkoutSessionUpdatesWorkoutData() = runTest {
    val uid = "testUid"
    val workoutSession = WorkoutSession("sessionId", 123L, 0L, SessionType.SOLO)

    val existingWorkoutData = WorkoutData(uid, listOf(workoutSession))
    `when`(document.exists()).thenReturn(true)
    `when`(document.toObject(WorkoutData::class.java)).thenReturn(existingWorkoutData)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))

    repository.addOrUpdateWorkoutSession(uid, workoutSession)

    verify(documentRef).update(eq(WORKOUT_SESSIONS), eq(listOf(workoutSession)))
  }

  @Test
  fun deleteWorkoutSessionRemovesSession() = runTest {
    val uid = "testUid"
    val sessionToDelete = WorkoutSession("sessionId", 123L, 0L, SessionType.SOLO)
    val remainingSession = WorkoutSession("remainingSessionId", 456L, 0L, SessionType.SOLO)

    val existingWorkoutData = WorkoutData(uid, listOf(sessionToDelete, remainingSession))

    // Create a list of session maps that mimics the structure retrieved from Firestore
    val sessionMaps =
        existingWorkoutData.workoutSessions.map { session ->
          mapOf(
              "sessionId" to session.sessionId,
              "startTime" to session.startTime,
              "endTime" to session.endTime,
              "sessionType" to session.sessionType.name,
              "participants" to session.participants,
              "exercises" to
                  session.exercises.map { exercise ->
                    mapOf(
                        "name" to exercise.name,
                        "reps" to exercise.reps,
                        "sets" to exercise.sets,
                        "weight" to exercise.weight,
                        "duration" to exercise.duration)
                  },
              "winner" to session.winner)
        }

    // Mock the Firestore interactions
    `when`(db.collection("workoutData")).thenReturn(collection)
    `when`(collection.document(uid)).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn(uid)
    `when`(document.get(WORKOUT_SESSIONS)).thenReturn(sessionMaps)

    // Perform the deletion
    repository.deleteWorkoutSession(uid, sessionToDelete.sessionId)

    // Capture the updated sessions list
    val captor = argumentCaptor<List<WorkoutSession>>()
    verify(documentRef).update(eq(WORKOUT_SESSIONS), captor.capture())

    println("Captured argument: ${captor.firstValue}")

    // Verify that only the remaining session is present
    assertEquals(listOf(remainingSession), captor.firstValue)
  }

  @Test
  fun updateWorkoutSessionDetailsUpdatesDetails() = runTest {
    val uid = "testUid"
    val sessionId = "sessionId"
    val exercises = listOf(Exercise(name = "Push-up"))
    val endTime = 123456789L

    repository.updateWorkoutSessionDetails(uid, sessionId, exercises, endTime)

    verify(documentRef)
        .update(
            mapOf(
                "workoutSessions.$sessionId.exercises" to exercises,
                "workoutSessions.$sessionId.endTime" to endTime))
  }

  @Test
  fun updateExerciseUpdatesCorrectFieldPath() = runTest {
    val uid = "testUid"
    val sessionId = "sessionId"
    val exerciseIndex = 0
    val updatedExercise = Exercise(name = "Pull-up", reps = 10, sets = 3)

    // Mock Firestore interactions
    `when`(db.collection(any())).thenReturn(collection)
    `when`(collection.document(any())).thenReturn(documentRef)

    // Call the function
    repository.updateExercise(uid, sessionId, exerciseIndex, updatedExercise)

    // Capture the update operation
    val captor = argumentCaptor<Map<String, Any>>()
    verify(documentRef).update(captor.capture())

    // Verify the field path and value
    val capturedUpdate = captor.firstValue
    val expectedFieldPath = "workoutSessions.$sessionId.exercises.$exerciseIndex"
    assertEquals(updatedExercise, capturedUpdate[expectedFieldPath])
  }

  @Test
  fun saveWorkoutDataSavesDataToFirestore() = runTest {
    val uid = "testUid"
    val workoutData =
        WorkoutData(
            userUid = uid,
            workoutSessions =
                listOf(
                    WorkoutSession(
                        sessionId = "sessionId1",
                        startTime = 123456789L,
                        endTime = 123456999L,
                        sessionType = SessionType.SOLO,
                        participants = listOf("user1", "user2"),
                        exercises =
                            listOf(
                                Exercise(
                                    name = "Push-up",
                                    reps = 10,
                                    sets = 3,
                                    weight = 20f,
                                    duration = 30)),
                        winner = null)))

    // Mock Firestore interactions
    `when`(db.collection(any())).thenReturn(collection)
    `when`(collection.document(any())).thenReturn(documentRef)

    // Call the function
    repository.saveWorkoutData(uid, workoutData)

    // Verify the data was saved to Firestore
    val captor = argumentCaptor<WorkoutData>()
    verify(documentRef).set(captor.capture())

    // Check the captured value matches the expected WorkoutData
    assertEquals(workoutData, captor.firstValue)
  }

  @Test
  fun sendPairingRequestSendsDataToFirestore() = runTest {
    val fromUid = "user1"
    val toUid = "user2"
    repository.sendPairingRequest(fromUid, toUid)

    val captor = argumentCaptor<PairingRequest>()
    verify(documentRef).set(captor.capture())

    assertEquals(fromUid, captor.firstValue.fromUid)
    assertEquals(toUid, captor.firstValue.toUid)
  }

  @Test
  fun deleteWorkoutDataByUidDeletesSingleDocument() = runTest {
    val uid = "testUid"
    val documentId = "docId1"

    // Mock the query snapshot and document
    val document = mock(DocumentSnapshot::class.java)
    val querySnapshot = mock(QuerySnapshot::class.java)
    val query = mock(Query::class.java)

    `when`(document.id).thenReturn(documentId)
    `when`(querySnapshot.documents).thenReturn(listOf(document))
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))
    `when`(db.collection(anyString()).whereEqualTo(anyString(), anyString())).thenReturn(query)
    `when`(db.collection(anyString()).document(anyString())).thenReturn(documentRef)

    // Call the function
    repository.deleteWorkoutDataByUid(uid)

    // Verify that the document was deleted
    verify(documentRef).delete()
  }

  @Test
  fun respondToPairingRequestUpdatesStatusAndCreatesSessionIfAccepted() = runTest {
    val requestId = "req123"
    val isAccepted = true
    val toUid = "toUser"
    val fromUid = "fromUser"

    whenever(documentRef.update(anyMap())).thenReturn(Tasks.forResult(null))
    // Mock set to return a successful Task instead of doNothing()
    whenever(documentRef.set(any())).thenReturn(Tasks.forResult(null))

    repository.respondToPairingRequest(requestId, isAccepted, toUid, fromUid)

    // Verify request status updated
    verify(db.collection(PAIRING_REQUESTS).document(requestId))
        .update(
            argThat<Map<String, Any>> { map ->
              map["status"] == RequestStatus.ACCEPTED.name &&
                  (map["sessionId"] as? String)?.startsWith("session_") == true
            })

    // Since accepted, a new session is created
    verify(documentRef, atLeastOnce()).update(eq("workoutSessions"), any<List<WorkoutSession>>())
  }

  @Test
  fun respondToPairingRequestRejectedUpdatesStatusOnly() = runTest {
    val requestId = "req123"
    val isAccepted = false
    val toUid = "toUser"
    val fromUid = "fromUser"

    whenever(documentRef.update(anyMap())).thenReturn(Tasks.forResult(null))
    repository.respondToPairingRequest(requestId, isAccepted, toUid, fromUid)

    // Verify request status updated to REJECTED
    verify(db.collection(PAIRING_REQUESTS).document(requestId))
        .update(
            argThat<Map<String, Any>> { map ->
              map["status"] == RequestStatus.REJECTED.name &&
                  (map["sessionId"] as? String)?.startsWith("session_") == true
            })
    // No new session should be created
    verify(documentRef, never()).set(any<WorkoutData>())
  }

  @Test
  fun generateSessionIdReturnsNonEmptyString() {
    val sessionId = repository.generateSessionId()
    assertTrue(sessionId.isNotEmpty())
    assertTrue(sessionId.startsWith("session_"))
  }

  @Test
  fun updateSessionAttributesUpdatesCorrectPaths() = runTest {
    val uid = "testUid"
    val sessionId = "session123"
    val updates = mapOf("winner" to "user123")

    repository.updateSessionAttributes(uid, sessionId, updates)

    val captor = argumentCaptor<Map<String, Any>>()
    verify(documentRef).update(captor.capture())

    // Check that "winner" is updated under "workoutSessions.session123.winner"
    assertEquals("user123", captor.firstValue["workoutSessions.$sessionId.winner"])
  }

  @Test
  fun updatePairingRequestStatusUpdatesFirestore() = runTest {
    val requestId = "req123"
    val status = RequestStatus.ACCEPTED

    repository.updatePairingRequestStatus(requestId, status)

    verify(db.collection(PAIRING_REQUESTS).document(requestId)).update("status", status.name)
  }
}
