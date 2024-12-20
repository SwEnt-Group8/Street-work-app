package com.android.streetworkapp.model.image

import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.storage.S3StorageClient
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlin.reflect.jvm.isAccessible
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
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
  @Mock private lateinit var mockCollection: CollectionReference
  @Mock private lateinit var mockDocumentRef: DocumentReference
  @Mock private lateinit var mockDocumentSnapshot: DocumentSnapshot

  @Captor
  private lateinit var parkImagesCaptor:
      ArgumentCaptor<List<ParkImage>> // for capturing firebase updates linked to list of ParkImages

  @Captor private lateinit var anyCaptor: ArgumentCaptor<FieldValue>
  private lateinit var parkImageCollection: ParkImageCollection

  private lateinit var imageRepositoryFirestore: ImageRepositoryFirestore
  private lateinit var spyImageRepositoryFirestore: ImageRepositoryFirestore
  private lateinit var parkImages: List<ParkImage>

  private val baseUser =
      User(uid = "baseUserUid", username = "baseUsername", "", 10, emptyList(), "", emptyList())

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    imageRepositoryFirestore =
        ImageRepositoryFirestore(fireStoreDB, storageClient, parkRepository, userRepository)

    spyImageRepositoryFirestore = spy(imageRepositoryFirestore)

    parkImages =
        listOf(
            ParkImage(
                imageUrl = "https://example.com/image1.jpg",
                userId = baseUser.uid,
                username = baseUser.username,
                rating =
                    ImageRating(
                        positiveVotes = 5,
                        negativeVotes = 2,
                        positiveVotesUids = listOf("userA", "userB", "userC", "userD", "userE"),
                        negativeVotesUids = listOf("userX", "userY")),
                uploadDate = Timestamp.now()),
            ParkImage(
                imageUrl = "https://example.com/image2.jpg",
                userId = "user456",
                username = "naturefan",
                rating =
                    ImageRating(
                        positiveVotes = 3,
                        negativeVotes = 1,
                        positiveVotesUids = listOf("userD", baseUser.uid, "userF"),
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

    whenever(parkRepository.getParkByPid("validParkId")).thenReturn(park)
    whenever(userRepository.getUserByUid("validUserId")).thenReturn(user)
    whenever(storageClient.uploadFile(any(), any())).thenReturn("http://dummyurl.com")
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document()).thenReturn(mockDocumentRef)
    whenever(mockCollection.document(any())).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.set(any())).thenReturn(Tasks.forResult(null))
    whenever(mockDocumentRef.id).thenReturn("newCollectionId")

    imageRepositoryFirestore.uploadImage("validIdentifier", ByteArray(10), park.pid, user.uid)

    verify(mockCollection).document()
    verify(mockDocumentRef).set(any())
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

    val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

    whenever(parkRepository.getParkByPid(park.pid)).thenReturn(park)
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(park.imagesCollectionId)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(false)

    val result = imageRepositoryFirestore.retrieveImages(park)

    assertTrue("Expected an empty list when the images collection does not exist", result.isEmpty())
  }

  @Test
  fun `retrieveImages returns list of ParkImage when valid data exists`() = runBlocking {
    val park = Park(pid = "validParkId", imagesCollectionId = "validNonEmptyCollectionId")

    whenever(parkRepository.getParkByPid(park.pid)).thenReturn(park)
    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(park.imagesCollectionId)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.exists()).thenReturn(true)
    whenever(mockDocumentSnapshot.toObject(ParkImageCollection::class.java))
        .thenReturn(parkImageCollection)

    val result = imageRepositoryFirestore.retrieveImages(park)

    assert(parkImages == result)
  }

  @Test
  fun `deleteImage deletes correct image from s3 storage and firebase entry`() = runBlocking {
    val dummyKeyExtractResult = "dummyKey"

    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(parkImageCollection.id)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.toObject(ParkImageCollection::class.java))
        .thenReturn(parkImageCollection)
    whenever(mockDocumentRef.update(eq("images"), any())).thenReturn(Tasks.forResult(null))
    whenever(
            storageClient.extractKeyFromUrl(
                parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl))
        .thenReturn(dummyKeyExtractResult)
    whenever(storageClient.deleteObjectFromKey(dummyKeyExtractResult)).thenReturn(true)

    imageRepositoryFirestore.deleteImage(
        parkImageCollection.id,
        parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl)
    verify(mockDocumentRef).update(eq("images"), anyCaptor.capture())
    verify(storageClient).deleteObjectFromKey(dummyKeyExtractResult)

    val capturedValue = anyCaptor.value
    val elementsField =
        capturedValue.javaClass.getDeclaredField("elements").apply { isAccessible = true }
    elementsField.isAccessible = true
    val elements = elementsField.get(capturedValue) as List<ParkImage>
    assert(elements[0] == parkImageCollection.images[parkImageCollection.images.size - 1])
  }

  @Test
  fun `imageVote sends correct updated ParkImage list to firebase`() = runBlocking {
    val votingUserUid = "votingUserUid"

    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(parkImageCollection.id)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.toObject(ParkImageCollection::class.java))
        .thenReturn(parkImageCollection)
    whenever(mockDocumentRef.update(eq("images"), any())).then { Tasks.forResult(null) }

    // positive vote
    imageRepositoryFirestore.imageVote(
        parkImageCollection.id,
        parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl,
        votingUserUid,
        VOTE_TYPE.POSITIVE)

    verify(mockDocumentRef).update(eq("images"), parkImagesCaptor.capture())
    val capturedUpdatedImagesPositiveVote = parkImagesCaptor.value

    val expectedParkImagesPositiveVote =
        parkImageCollection.images.mapIndexed { index, parkImage ->
          if (index != parkImageCollection.images.size - 1) return@mapIndexed parkImage

          parkImage.copy(
              rating =
                  parkImage.rating.copy(
                      positiveVotes = parkImage.rating.positiveVotes + VOTE_TYPE.POSITIVE.value,
                      positiveVotesUids = parkImage.rating.positiveVotesUids + votingUserUid))
        }

    assert(capturedUpdatedImagesPositiveVote == expectedParkImagesPositiveVote)

    // negative vote
    imageRepositoryFirestore.imageVote(
        parkImageCollection.id,
        parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl,
        votingUserUid,
        VOTE_TYPE.NEGATIVE)

    verify(mockDocumentRef, times(2)).update(eq("images"), parkImagesCaptor.capture())
    val capturedUpdatedImagesNegativeVote = parkImagesCaptor.value

    val expectedParkImagesNegativeVote =
        parkImageCollection.images.mapIndexed { index, parkImage ->
          if (index != parkImageCollection.images.size - 1) return@mapIndexed parkImage

          parkImage.copy(
              rating =
                  parkImage.rating.copy(
                      negativeVotes = parkImage.rating.negativeVotes + VOTE_TYPE.NEGATIVE.value,
                      negativeVotesUids = parkImage.rating.negativeVotesUids + votingUserUid))
        }

    assert(capturedUpdatedImagesNegativeVote == expectedParkImagesNegativeVote)
  }

  @Test
  fun `retractImageVote sends correct updated ParkImage list to firebase`() = runBlocking {
    val votingUserUid =
        parkImageCollection.images[parkImageCollection.images.size - 1]
            .rating
            .positiveVotesUids[0] // pick the first user id from the list

    whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
        .thenReturn(mockCollection)
    whenever(mockCollection.document(parkImageCollection.id)).thenReturn(mockDocumentRef)
    whenever(mockDocumentRef.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot))
    whenever(mockDocumentSnapshot.toObject(ParkImageCollection::class.java))
        .thenReturn(parkImageCollection)
    whenever(mockDocumentRef.update(eq("images"), any())).then { Unit }

    // retract positive vote
    imageRepositoryFirestore.retractImageVote(
        parkImageCollection.id,
        parkImageCollection.images[parkImageCollection.images.size - 1].imageUrl,
        votingUserUid)

    verify(mockDocumentRef).update(eq("images"), parkImagesCaptor.capture())
    val capturedUpdatedImagesPositiveVote = parkImagesCaptor.value

    val expectedParkImagesAfterRetract =
        parkImageCollection.images.mapIndexed { index, parkImage ->
          if (index != parkImageCollection.images.size - 1) return@mapIndexed parkImage

          parkImage.copy(
              rating =
                  parkImage.rating.copy(
                      positiveVotes = parkImage.rating.positiveVotes - VOTE_TYPE.POSITIVE.value,
                      positiveVotesUids = parkImage.rating.positiveVotesUids - votingUserUid))
        }

    assert(capturedUpdatedImagesPositiveVote == expectedParkImagesAfterRetract)
  }

  @Test
  fun `deleteAllDataFromUser correctly removes images across collections and removes user votes`() =
      runTest {
        val imageToDeleteKey = "dummyKey"
        val imageToBeDeleted = parkImages[0] // image containing baseUser as owner
        val imageWhereVoteHasToBeRemoved = parkImages[1]
        val mockQuerySnapshot = mock(QuerySnapshot::class.java)
        val mockDocumentSnapshotList =
            listOf(mock(DocumentSnapshot::class.java), mock(DocumentSnapshot::class.java))
        val mockDocumentRefList =
            listOf(mock(DocumentReference::class.java), mock(DocumentReference::class.java))

        val parkImagesCollections =
            listOf(
                ParkImageCollection(id = "collection1", images = listOf(imageToBeDeleted)),
                ParkImageCollection("collection2", images = listOf(imageWhereVoteHasToBeRemoved)))

        whenever(fireStoreDB.collection(ImageRepositoryFirestore.COLLECTION_PATH))
            .thenReturn(mockCollection)

        whenever(mockCollection.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))
        whenever(mockQuerySnapshot.documents).thenReturn(mockDocumentSnapshotList)

        for ((index, docSnapshot) in mockDocumentSnapshotList.withIndex()) {
          whenever(docSnapshot.toObject(ParkImageCollection::class.java))
              .thenReturn(parkImagesCollections[index])

          whenever(docSnapshot.reference).thenReturn(mockDocumentRefList[index])
        }

        whenever(storageClient.extractKeyFromUrl(imageToBeDeleted.imageUrl))
            .thenReturn(imageToDeleteKey)
        whenever(storageClient.deleteObjectFromKey(imageToDeleteKey)).thenReturn(true)
        whenever(mockDocumentRefList[0].update(eq("images"), any()))
            .thenReturn(Tasks.forResult(null))
        whenever(
                spyImageRepositoryFirestore.retractImageVote(
                    parkImagesCollections[1].id,
                    imageWhereVoteHasToBeRemoved.imageUrl,
                    baseUser.uid))
            .thenReturn(
                true) // I'm not remocking the whole thing for retract vote, it's already tested
        // above

        spyImageRepositoryFirestore.deleteAllDataFromUser(
            baseUser.uid) // call the function to be tested

        verify(mockDocumentRefList[0]).update(eq("images"), anyCaptor.capture())

        val capturedValue = anyCaptor.value
        val elementsField =
            capturedValue.javaClass.getDeclaredField("elements").apply { isAccessible = true }
        elementsField.isAccessible = true
        val elements = elementsField.get(capturedValue) as List<ParkImage>
        assert(elements[0] == imageToBeDeleted)

        verify(storageClient).deleteObjectFromKey(imageToDeleteKey)
        verify(spyImageRepositoryFirestore)
            .retractImageVote(
                parkImagesCollections[1].id, imageWhereVoteHasToBeRemoved.imageUrl, baseUser.uid)
      }

  @Test
  fun `deleteImage returns false on handled exception`() = runTest {
    assert(!imageRepositoryFirestore.deleteImage("nonEmptyId", "https.//dummyurl.com"))
  }

  @Test
  fun `imageVote returns false on handled exception`() = runTest {
    assert(
        !imageRepositoryFirestore.imageVote(
            "nonEmptyId", "https://dummyurl.com", "dummyVoterUID", VOTE_TYPE.POSITIVE))
  }

  @Test
  fun `deleteAllDataFromUser returns false on handled exception`() = runTest {
    assert(!imageRepositoryFirestore.deleteAllDataFromUser("nonEmptyId"))
  }

  @Test
  fun `registerCollectionListener with non empty collection id doesn't crash on exception`() {
    imageRepositoryFirestore.registerCollectionListener("nonEmptyId") {}
  }
}
