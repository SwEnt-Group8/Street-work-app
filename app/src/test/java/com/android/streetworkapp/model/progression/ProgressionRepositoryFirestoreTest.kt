package com.android.streetworkapp.model.progression

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
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
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProgressionRepositoryFirestoreTest {

  @Mock private lateinit var db: FirebaseFirestore
  @Mock private lateinit var collection: CollectionReference
  @Mock private lateinit var documentRef: DocumentReference
  @Mock private lateinit var document: DocumentSnapshot
  @Mock private lateinit var query: QuerySnapshot

  private lateinit var progressionRepository: ProgressionRepositoryFirestore
  private lateinit var progression: Progression

  @Before
  fun setUp() {

    MockitoAnnotations.openMocks(this)

    // Initialize Firebase if necessary
    if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
      FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
    }

    progressionRepository = ProgressionRepositoryFirestore(db)

    whenever(db.collection("progressions")).thenReturn(collection)
    `when`(db.collection(any())).thenReturn(collection)
    `when`(collection.document(any())).thenReturn(documentRef)
    `when`(collection.document()).thenReturn(documentRef)
  }

  @Test
  fun getNewProgressionIdReturnsUniqueId() {
    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueProgressionId")

    val progressionId = progressionRepository.getNewProgressionId()
    assertEquals("uniqueProgressionId", progressionId)
  }

  @Test
  fun addEventAddsEventSuccessfully() = runTest {
    `when`(collection.document("test")).thenReturn(documentRef)
    `when`(documentRef.set("test")).thenReturn(Tasks.forResult(null))

    progressionRepository.createProgression("test", "test")
    verify(documentRef).set(Progression("test", "test", Ranks.BRONZE.score))
  }

  @Test
  fun documentToProgressionTest() = runTest {
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("test")
    `when`(document["uid"]).thenReturn("test")
    `when`(document["currentGoal"]).thenReturn(100)
    `when`(document["eventsCreated"]).thenReturn(0)
    `when`(document["eventsJoined"]).thenReturn(0)
    `when`(document["achievements"]).thenReturn(emptyList<Achievement>())

    `when`(collection.whereEqualTo("uid", "test")).thenReturn(collection)

    `when`(db.collection("progressions")).thenReturn(collection)
    `when`(collection.document("test")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    var progression = Progression()
    progressionRepository.getProgression("test", { p -> progression = p }, {})

    verify(collection).whereEqualTo("uid", "test")
  }

  @Test
  fun getProgressionTest() = runTest {

    // Ensure that mockToDoQuerySnapshot is properly initialized and mocked
    `when`(collection.get()).thenReturn(Tasks.forResult(query))
    `when`(collection.whereEqualTo("uid", "test")).thenReturn(collection)

    // Ensure the QuerySnapshot returns a list of mock DocumentSnapshots
    `when`(query.documents).thenReturn(listOf())

    val onSuccess: (Progression) -> Unit = {}
    val onFailure: (Exception) -> Unit = {}
    progressionRepository.getProgression("test", onSuccess, onFailure)

    // Verify that the 'documents' field was accessed
    org.mockito.kotlin.verify(timeout(100)) { (query).documents }
    verify(collection).get()
  }

  @Test
  fun AchievementAndGoalTest() = runTest {
    `when`(collection.document("test")).thenReturn(documentRef)
    // check .update("currentGoal", goal, "achievements", achievements)
    `when`(documentRef.update("currentGoal", 0, "achievements", emptyList<Achievement>()))
        .thenReturn(Tasks.forResult(null))

    progressionRepository.updateProgressionWithAchievementAndGoal("test", emptyList(), 0)
    verify(documentRef).update("currentGoal", 0, "achievements", emptyList<Achievement>())
  }
}
