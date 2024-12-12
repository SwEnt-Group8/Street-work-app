package com.android.streetworkapp.model.progression

import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.TaskCompletionSource
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
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

    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("test")
    `when`(document["uid"]).thenReturn("test")
    `when`(document["currentGoal"]).thenReturn(100)
    `when`(document["eventsCreated"]).thenReturn(0)
    `when`(document["eventsJoined"]).thenReturn(0)
    `when`(document["achievements"]).thenReturn(emptyList<Achievement>())
  }

  @Test
  fun getNewProgressionIdReturnsUniqueId() {
    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueProgressionId")

    val progressionId = progressionRepository.getNewProgressionId()
    assertEquals("uniqueProgressionId", progressionId)
  }

  @Test
  fun AchievementAndGoalTest() = runTest {
    `when`(collection.document("test")).thenReturn(documentRef)
    `when`(documentRef.update("currentGoal", 0, "achievements", emptyList<Achievement>()))
        .thenReturn(Tasks.forResult(null))

    progressionRepository.updateProgressionWithAchievementAndGoal("test", emptyList(), 0)
    verify(documentRef).update("currentGoal", 0, "achievements", emptyList<Achievement>())
  }

  @Test
  fun documentToProgressionTest2() = runTest {
    `when`(collection.whereEqualTo("uid", "test")).thenReturn(collection)

    `when`(db.collection("progressions")).thenReturn(collection)
    `when`(collection.document("test")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    val result = progressionRepository.documentToProgression(document)

    assert(result.progressionId == "test")
    assert(result.uid == "test")
    assert(result.achievements == emptyList<Achievement>())
  }

  @Test
  fun deleteProgressionByUidDeletesDocument() = runTest {
    val uid = "testUid"
    val documentId = "docId1"

    // Mock the query snapshot and document
    val document = mock(DocumentSnapshot::class.java)
    val querySnapshot = mock(QuerySnapshot::class.java)
    val query = mock(Query::class.java)

    `when`(document.id).thenReturn(documentId)
    `when`(querySnapshot.documents).thenReturn(listOf(document))
    `when`(querySnapshot.isEmpty).thenReturn(false)
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))
    `when`(db.collection(anyString()).whereEqualTo(anyString(), anyString())).thenReturn(query)
    `when`(db.collection(anyString()).document(anyString())).thenReturn(documentRef)

    // Call the function
    progressionRepository.deleteProgressionByUid(uid)

    // Verify that the document was deleted
    verify(documentRef).delete()
  }
}
