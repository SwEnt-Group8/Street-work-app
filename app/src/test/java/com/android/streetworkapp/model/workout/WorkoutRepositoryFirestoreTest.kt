package com.android.streetworkapp.model.workout

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WorkoutRepositoryFirestoreTest {

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var collection: CollectionReference
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var document: DocumentSnapshot
  @Mock private lateinit var mockSnapshot: QuerySnapshot
  @Mock private lateinit var mockListenerRegistration: ListenerRegistration

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
    `when`(document.data).thenReturn(mapOf("workoutSessions" to emptyList<Map<String, Any>>()))
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

    verify(documentRef).update(eq("workoutSessions"), eq(listOf(workoutSession)))
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
    `when`(document.get("workoutSessions")).thenReturn(sessionMaps)

    // Perform the deletion
    repository.deleteWorkoutSession(uid, sessionToDelete.sessionId)

    // Capture the updated sessions list
    val captor = argumentCaptor<List<WorkoutSession>>()
    verify(documentRef).update(eq("workoutSessions"), captor.capture())

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
  fun respondToPairingRequestUpdatesStatus() = runTest {
    val requestId = "req1"
    val isAccepted = true
    repository.respondToPairingRequest(requestId, isAccepted)

    verify(db.collection("pairingRequests").document(requestId))
        .update("status", RequestStatus.ACCEPTED.name)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun observePairingRequestsEmitsCorrectValues() = runTest {
    val uid = "testUid"

    // Mocks
    val mockQuery = mock(Query::class.java)
    val mockListenerRegistration = mock(ListenerRegistration::class.java)
    val mockSnapshot = mock(QuerySnapshot::class.java)
    val mockPairingRequest1 = PairingRequest("req1", "user1", uid, RequestStatus.PENDING)
    val mockPairingRequest2 = PairingRequest("req2", "user2", uid, RequestStatus.ACCEPTED)

    whenever(collection.whereEqualTo("toUid", uid)).thenReturn(mockQuery)
    whenever(mockQuery.addSnapshotListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as EventListener<QuerySnapshot>
      // Simulate receiving a snapshot
      listener.onEvent(mockSnapshot, null)
      mockListenerRegistration
    }
    whenever(mockSnapshot.isEmpty).thenReturn(false)
    whenever(mockSnapshot.toObjects(PairingRequest::class.java))
        .thenReturn(listOf(mockPairingRequest1, mockPairingRequest2))

    val result = mutableListOf<List<PairingRequest>>()

    // Launch a job to collect the flow
    val job = launch { repository.observePairingRequests(uid).collect { result.add(it) } }

    // Advance time if needed
    advanceUntilIdle()

    // Verify that we received our expected emission
    assertEquals(1, result.size)
    assertEquals(listOf(mockPairingRequest1, mockPairingRequest2), result[0])

    // Cancel the job after verification to let the test complete
    job.cancelAndJoin()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun observeWorkoutSessionsEmitsCorrectValues() = runTest {
    val uid = "testUid"
    val sessionsCollection = mock(CollectionReference::class.java)
    val mockListenerRegistration = mock(ListenerRegistration::class.java)
    val mockSnapshot = mock(QuerySnapshot::class.java)
    val mockSession1 =
        WorkoutSession(
            "session1", startTime = 1000L, endTime = 2000L, sessionType = SessionType.SOLO)
    val mockSession2 =
        WorkoutSession(
            "session2", startTime = 2000L, endTime = 3000L, sessionType = SessionType.COACH)

    // Mock the chain: db.collection(COLLECTION_PATH).document(uid).collection(WORKOUT_SESSIONS)
    whenever(db.collection(COLLECTION_PATH)).thenReturn(collection)
    whenever(collection.document(uid)).thenReturn(documentRef)
    whenever(documentRef.collection(WORKOUT_SESSIONS)).thenReturn(sessionsCollection)

    // Mock addSnapshotListener on sessionsCollection
    whenever(sessionsCollection.addSnapshotListener(any())).thenAnswer { invocation ->
      val listener = invocation.arguments[0] as EventListener<QuerySnapshot>
      // Simulate receiving a snapshot
      listener.onEvent(mockSnapshot, null)
      mockListenerRegistration
    }

    whenever(mockSnapshot.toObjects(WorkoutSession::class.java))
        .thenReturn(listOf(mockSession1, mockSession2))

    val result = mutableListOf<List<WorkoutSession>>()

    val job = launch {
      repository.observeWorkoutSessions(uid).collect { sessions -> result.add(sessions) }
    }

    advanceUntilIdle()

    // After triggering the event once, we should have one emission
    assertEquals(1, result.size)
    assertEquals(listOf(mockSession1, mockSession2), result[0])

    job.cancelAndJoin()
  }

  @Test
  fun addCommentToSessionAddsCommentSuccessfully() = runTest {
    val sessionId = "session123"
    val comment = Comment(authorUid = "user123", text = "Great session!")

    val sessionPath = "$COLLECTION_PATH/${comment.authorUid}/$WORKOUT_SESSIONS/$sessionId/comments"
    val commentsCollection = mock(CollectionReference::class.java)
    val mockTask: Task<DocumentReference> = Tasks.forResult(documentRef)

    // Mock db.collection(sessionPath)
    whenever(db.collection(sessionPath)).thenReturn(commentsCollection)
    whenever(commentsCollection.add(comment)).thenReturn(mockTask)

    // No exception should be thrown
    repository.addCommentToSession(sessionId, comment)

    // Verify that db.collection(...) and add(...) were called correctly
    verify(db).collection(eq(sessionPath))
    verify(commentsCollection).add(eq(comment))
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
}
