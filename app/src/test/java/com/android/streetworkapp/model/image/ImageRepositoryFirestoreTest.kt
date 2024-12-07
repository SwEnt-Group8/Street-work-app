package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryFirestoreTest {
  @Mock private lateinit var fireStoreDB: FirebaseFirestore
  @Mock private lateinit var parkRepository: ParkRepository
  @Mock private lateinit var userRepository: UserRepository

  private lateinit var imageRepositoryFirestore: ImageRepositoryFirestore

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    imageRepositoryFirestore = ImageRepositoryFirestore(fireStoreDB, parkRepository, userRepository)
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when imageB64 is empty`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { imageRepositoryFirestore.uploadImage("", "validParkId", "validUserId") }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when parkId is empty`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { imageRepositoryFirestore.uploadImage("validBase64", "", "validUserId") }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when userId is invalid`() {
    wheneverBlocking { userRepository.getUserByUid("invalidUserId") }.thenReturn(null)

    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        imageRepositoryFirestore.uploadImage("validBase64", "validParkId", "invalidUserId")
      }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when parkId is invalid`() {
    wheneverBlocking { parkRepository.getParkByPid("invalidParkId") }.thenReturn(null)

    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        imageRepositoryFirestore.uploadImage("validBase64", "invalidParkId", "validUserId")
      }
    }
  }

  @Test
  fun `uploadImage creates a new image collection if none exists`() = runBlocking {
    val park = Park(pid = "validParkId", imagesCollectionId = "")
    val mockUser = mock(User::class.java)
    val mockDocument = mock(DocumentReference::class.java)
    val mockCollection = mock(CollectionReference::class.java)
    val mockTask = mock(Task::class.java) as Task<Void>

    whenever(mockTask.isComplete).thenReturn(true)
    whenever(parkRepository.getParkByPid("validParkId")).thenReturn(park)
    whenever(userRepository.getUserByUid("validUserId")).thenReturn(mockUser)
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document()).thenReturn(mockDocument)
    whenever(mockCollection.document(any())).thenReturn(mockDocument)
    whenever(mockDocument.set(any())).thenReturn(mockTask)
    whenever(mockDocument.id).thenReturn("newCollectionId")

    imageRepositoryFirestore.uploadImage("validBase64", "validParkId", "validUserId")

    verify(mockCollection).document()
    verify(mockDocument).set(any())
    verify(parkRepository).addImagesCollection("validParkId", "newCollectionId")
  }

  @Test
  fun `retrieveImages returns empty list when park has no imagesCollectionId`() = runBlocking {
    val park = Park(pid = "validParkId", imagesCollectionId = "")

    whenever(parkRepository.getParkByPid("validParkId")).thenReturn(park)
    val result = imageRepositoryFirestore.retrieveImages(park)

    assertTrue("Expected an empty list when imagesCollectionId is empty", result.isEmpty())
  }

  @Test
  fun `retrieveImages returns empty list when images collection does not exist`() = runBlocking {
    val park = Park(pid = "validParkId", imagesCollectionId = "missingCollectionId")

    val mockCollection = mock(CollectionReference::class.java)
    val mockDocument = mock(DocumentReference::class.java)
    val mockTask = mock(Task::class.java) as Task<DocumentSnapshot>
    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    whenever(parkRepository.getParkByPid(park.pid)).thenReturn(park)
    whenever(mockTask.isComplete).thenReturn(true)
    whenever(mockTask.result).thenReturn(mockDocumentSnapshot)
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(park.imagesCollectionId)).thenReturn(mockDocument)
    whenever(mockDocument.get()).thenReturn(mockTask)
    whenever(mockDocumentSnapshot.exists()).thenReturn(false)

    val result = imageRepositoryFirestore.retrieveImages(park)

    assertTrue("Expected an empty list when the images collection does not exist", result.isEmpty())
  }

    @Test
    fun `retrieveImages returns list of ParkImageDatabase when valid data exists`() = runBlocking {
        val mockImagesReply = listOf(
            mapOf(
                "imageB64" to "imageData1",
                "userId" to "user1",
                "rating" to mapOf("first" to 5, "second" to 10),
                "uploadDate" to Timestamp.now()
            ),
            mapOf(
                "imageB64" to "imageData2",
                "userId" to "user2",
                "rating" to mapOf("first" to 8, "second" to 12),
                "uploadDate" to Timestamp.now()
            )
        )

        val park = Park(pid = "validParkId", imagesCollectionId = "validNonEmptyCollectionId")

        val mockCollection = mock(CollectionReference::class.java)
        val mockDocument = mock(DocumentReference::class.java)
        val mockTask = mock(Task::class.java) as Task<DocumentSnapshot>
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        whenever(parkRepository.getParkByPid(park.pid)).thenReturn(park)
        whenever(mockTask.isComplete).thenReturn(true)
        whenever(mockTask.result).thenReturn(mockDocumentSnapshot)
        whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
            .thenReturn(mockCollection)
        whenever(mockCollection.document(park.imagesCollectionId)).thenReturn(mockDocument)
        whenever(mockDocument.get()).thenReturn(mockTask)
        whenever(mockDocumentSnapshot.exists()).thenReturn(true)
        whenever(mockDocumentSnapshot.get("images")).thenReturn(mockImagesReply)

        val result = imageRepositoryFirestore.retrieveImages(park)

        assertEquals(mockImagesReply.size, result.size)

        for ((index, image) in mockImagesReply.withIndex()) {
            val first =  (image["rating"] as? Map<*, *>)?.get("first") as? Int
            val second = (image["rating"] as? Map<*, *>)?.get("second") as? Int

            assertEquals(image["imageB64"], result[index].imageB64)
            assertEquals(image["userId"], result[index].userId)
            assertEquals(Pair(first, second), result[index].rating)
        }
    }
}
