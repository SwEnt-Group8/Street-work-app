package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.storage.S3StorageClient
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageRepositoryFirestoreTest {
  @Mock private lateinit var fireStoreDB: FirebaseFirestore
  @Mock private lateinit var parkRepository: ParkRepository
  @Mock private lateinit var userRepository: UserRepository
  @Mock private lateinit var storageClient: S3StorageClient

  @Captor
  private lateinit var parkImagesCaptor:
      ArgumentCaptor<List<ParkImage>> // for capturing firebase updates etc...

  private lateinit var parkImageCollection: ParkImageCollection

  private lateinit var imageRepositoryFirestore: ImageRepositoryFirestore
  private lateinit var parkImages: List<ParkImage>

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    imageRepositoryFirestore =
        ImageRepositoryFirestore(fireStoreDB, storageClient, parkRepository, userRepository)

    parkImages =
        listOf(
            ParkImage(
                imageUrl = "https://example.com/image1.jpg",
                userId = "user123",
                username = "parklover",
                rating =
                    ImageRating(
                        positiveVotes = 10,
                        negativeVotes = 2,
                        positiveVotesUids = listOf("userA", "userB", "userC"),
                        negativeVotesUids = listOf("userX", "userY")),
                uploadDate = Timestamp.now()),
            ParkImage(
                imageUrl = "https://example.com/image2.jpg",
                userId = "user456",
                username = "naturefan",
                rating =
                    ImageRating(
                        positiveVotes = 5,
                        negativeVotes = 1,
                        positiveVotesUids = listOf("userD", "userE"),
                        negativeVotesUids = listOf("userZ")),
                uploadDate = Timestamp.now()))

    parkImageCollection = ParkImageCollection(id = "collection123", images = parkImages)
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when uniqueImageIdentifier is empty`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking { imageRepositoryFirestore.uploadImage("", ByteArray(10), "parkId", "userId") }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when parkId is empty`() {
    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        imageRepositoryFirestore.uploadImage("validIdentifier", ByteArray(10), "", "userId")
      }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when userId is invalid`() {
    wheneverBlocking { userRepository.getUserByUid("invalidUserId") }.thenReturn(null)

    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        imageRepositoryFirestore.uploadImage(
            "validIdentifier", ByteArray(10), "validParkId", "invalidUserId")
      }
    }
  }

  @Test
  fun `uploadImage throws IllegalArgumentException when parkId is invalid`() {
    wheneverBlocking { parkRepository.getParkByPid("invalidParkId") }.thenReturn(null)

    assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        imageRepositoryFirestore.uploadImage(
            "validIdentifier", ByteArray(10), "invalidParkId", "validUserId")
      }
    }
  }

  @Test
  fun `uploadImage creates a new image collection if none exists`() = runBlocking {
    val park = Park(pid = "validParkId", imagesCollectionId = "")
    val user = User(uid = "validUserId", "name", "", 20, emptyList(), "")
    val mockDocument = mock(DocumentReference::class.java)
    val mockCollection = mock(CollectionReference::class.java)
    val mockTask = mock(Task::class.java) as Task<Void>

    whenever(mockTask.isComplete).thenReturn(true)
    whenever(parkRepository.getParkByPid("validParkId")).thenReturn(park)
    whenever(userRepository.getUserByUid("validUserId")).thenReturn(user)
    whenever(storageClient.uploadFile(any(), any())).thenReturn("http://dummyurl.com")
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document()).thenReturn(mockDocument)
    whenever(mockCollection.document(any())).thenReturn(mockDocument)
    whenever(mockDocument.set(any())).thenReturn(mockTask)
    whenever(mockDocument.id).thenReturn("newCollectionId")

    imageRepositoryFirestore.uploadImage("validIdentifier", ByteArray(10), park.pid, user.uid)

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
  fun `retrieveImages returns list of ParkImage when valid data exists`() = runBlocking {
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
    whenever(mockDocumentSnapshot.toObject(ParkImageCollection::class.java))
        .thenReturn(parkImageCollection)

    val result = imageRepositoryFirestore.retrieveImages(park)

    assert(parkImages == result)
  }

  @Test
  fun `imageVote sends correct updated ParkImage list to firebase`() = runBlocking {
    val votingUserUid = "votingUserUid"
    val mockCollection = mock(CollectionReference::class.java)
    val mockDocumentRef = mock(DocumentReference::class.java)
    val mockSnapshot = mock(DocumentSnapshot::class.java)
    val mockTask = mock(Task::class.java) as Task<DocumentSnapshot>
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(parkImageCollection.id)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(mockTask)
    whenever(mockTask.isComplete).thenReturn(true)
    whenever(mockTask.isSuccessful).thenReturn(true)
    whenever(mockTask.result).thenReturn(mockSnapshot)
    whenever(mockSnapshot.toObject(ParkImageCollection::class.java)).thenReturn(parkImageCollection)
    whenever(mockDocumentRef.update(eq("images"), any())).then { Unit }

    imageRepositoryFirestore.imageVote(
        parkImageCollection.id,
        parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl,
        votingUserUid,
        VOTE_TYPE.POSITIVE)

    verify(mockDocumentRef).update(eq("images"), parkImagesCaptor.capture())
    val capturedUpdatedImages = parkImagesCaptor.value

    val expectedParkImages =
        parkImageCollection.images.mapIndexed { index, parkImage ->
          if (index != parkImageCollection.images.size - 1) return@mapIndexed parkImage

          parkImage.copy(
              rating =
                  parkImage.rating.copy(
                      positiveVotes = parkImage.rating.positiveVotes + VOTE_TYPE.POSITIVE.value,
                      positiveVotesUids = parkImage.rating.positiveVotesUids + votingUserUid))
        }

    assert(capturedUpdatedImages == expectedParkImages)
  }
}
