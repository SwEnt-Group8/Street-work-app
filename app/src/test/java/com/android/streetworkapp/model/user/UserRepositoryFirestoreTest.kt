package com.android.streetworkapp.model.user

import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class UserRepositoryFirestoreTest {

  private lateinit var db: FirebaseFirestore
  private lateinit var userRepository: UserRepositoryFirestore

  private lateinit var collection: CollectionReference
  private lateinit var documentRef: DocumentReference
  private lateinit var document: DocumentSnapshot
  private lateinit var batch: WriteBatch

  @Before
  fun setUp() {
    db = mock(FirebaseFirestore::class.java)
    userRepository = UserRepositoryFirestore(db)
    collection = mock()
    documentRef = mock()
    document = mock()
    batch = mock()

    whenever(db.collection("users")).thenReturn(collection)
    whenever(document.getString("picture")).thenReturn("")
  }

  @Test
  fun getNewUidReturnsUniqueId() {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueId")

    val uid = userRepository.getNewUid()
    assertEquals("uniqueId", uid)
  }

  @Test
  fun getUserByUidWithValidUidReturnsUser() = runTest {
    // Setup the DocumentSnapshot
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("username")).thenReturn("John Doe")
    `when`(document.getString("email")).thenReturn("john.doe@example.com")
    `when`(document.getLong("score")).thenReturn(100L)
    `when`(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    // Mock Firestore interactions
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)

    // Use TaskCompletionSource to create a controllable Task
    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    // Call the repository method
    val user = userRepository.getUserByUid("123")

    // Log user result for debugging
    println("User: $user")

    // Assert the result is not null and contains expected values
    assertNotNull(user)
    assertEquals("123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john.doe@example.com", user?.email)
    assertEquals(100, user?.score)
    assertEquals(listOf("friend1", "friend2"), user?.friends)
  }

  @Test
  fun getUserByUidWithInvalidUidReturnsNull() = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("invalid")).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forException(Exception("User not found")))

    val user = userRepository.getUserByUid("invalid")
    assertNull(user)
  }

  @Test
  fun getFriendsByUidWithValidUidReturnsListOfFriends() = runTest {
    // Mock User DocumentSnapshot
    val userDocument = mock<DocumentSnapshot>()
    whenever(userDocument.exists()).thenReturn(true)
    whenever(userDocument.id).thenReturn("user123")
    whenever(userDocument.get("friends")).thenReturn(listOf("friend1", "friend2"))

    // Mock Task for user document retrieval
    val userDocumentTaskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    userDocumentTaskCompletionSource.setResult(userDocument)
    val userDocumentTask = userDocumentTaskCompletionSource.task

    // Mock CollectionReference and DocumentReference for user
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("user123")).thenReturn(documentRef)
    whenever(documentRef.get()).thenReturn(userDocumentTask)

    // Mock Friends' DocumentSnapshots
    val friendDocument1 = mock<DocumentSnapshot>()
    whenever(friendDocument1.exists()).thenReturn(true)
    whenever(friendDocument1.id).thenReturn("friend1")
    whenever(friendDocument1.getString("username")).thenReturn("Friend One")
    whenever(friendDocument1.getString("email")).thenReturn("friend1@example.com")
    whenever(friendDocument1.getLong("score")).thenReturn(50L)
    whenever(friendDocument1.get("friends")).thenReturn(emptyList<String>())
    whenever(friendDocument1.getString("picture")).thenReturn("")

    val friendDocument2 = mock<DocumentSnapshot>()
    whenever(friendDocument2.exists()).thenReturn(true)
    whenever(friendDocument2.id).thenReturn("friend2")
    whenever(friendDocument2.getString("username")).thenReturn("Friend Two")
    whenever(friendDocument2.getString("email")).thenReturn("friend2@example.com")
    whenever(friendDocument2.getLong("score")).thenReturn(60L)
    whenever(friendDocument2.get("friends")).thenReturn(emptyList<String>())
    whenever(friendDocument2.getString("picture")).thenReturn("")

    // Mock QuerySnapshot for friends
    val friendsQuerySnapshot = mock<QuerySnapshot>()
    whenever(friendsQuerySnapshot.documents).thenReturn(listOf(friendDocument1, friendDocument2))

    // Mock Task for friends query
    val friendsQueryTaskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    friendsQueryTaskCompletionSource.setResult(friendsQuerySnapshot)
    val friendsQueryTask = friendsQueryTaskCompletionSource.task

    // Mock Query for friends retrieval
    val friendsQuery = mock<Query>()
    whenever(collection.whereIn(FieldPath.documentId(), listOf("friend1", "friend2")))
        .thenReturn(friendsQuery)
    whenever(friendsQuery.get()).thenReturn(friendsQueryTask)

    // Call the repository method
    val friendsList = userRepository.getFriendsByUid("user123")

    // Assert the result is not null and contains expected values
    assertNotNull(friendsList)
    assertEquals(2, friendsList?.size)

    val friend1 = friendsList?.find { it.uid == "friend1" }
    val friend2 = friendsList?.find { it.uid == "friend2" }

    assertNotNull(friend1)
    assertEquals("Friend One", friend1?.username)
    assertEquals("friend1@example.com", friend1?.email)
    assertEquals(50, friend1?.score)
    assertTrue(friend1?.friends?.isEmpty() == true)
    assertEquals("", friend1?.picture)

    assertNotNull(friend2)
    assertEquals("Friend Two", friend2?.username)
    assertEquals("friend2@example.com", friend2?.email)
    assertEquals(60, friend2?.score)
    assertTrue(friend2?.friends?.isEmpty() == true)
    assertEquals("", friend1?.picture)
  }

  @Test
  fun getParksByUidWithValidUidReturnsListOfParks() = runTest {
    // Mock User DocumentSnapshot
    val userDocument = mock<DocumentSnapshot>()
    whenever(userDocument.exists()).thenReturn(true)
    whenever(userDocument.id).thenReturn("user123")
    whenever(userDocument.get("parks")).thenReturn(listOf("park1", "park2"))

    // Mock Task for user document retrieval
    val userDocumentTaskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    userDocumentTaskCompletionSource.setResult(userDocument)
    val userDocumentTask = userDocumentTaskCompletionSource.task

    // Mock CollectionReference and DocumentReference for user
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("user123")).thenReturn(documentRef)
    whenever(documentRef.get()).thenReturn(userDocumentTask)

    // Call the repository method
    val parksList = userRepository.getParksByUid("user123")

    // Assert the result is not null and contains expected values
    assertNotNull(parksList)
    assertEquals(2, parksList?.size)

    assertEquals("park1", parksList?.get(0))
    assertEquals("park2", parksList?.get(1))
  }

  @Test
  fun getUserByEmailWithValidEmailReturnsUser() = runBlocking {
    // Setup the DocumentSnapshot
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("username")).thenReturn("John Doe")
    `when`(document.getString("email")).thenReturn("john.doe@example.com")
    `when`(document.getLong("score")).thenReturn(100L)
    `when`(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(listOf(document))

    val query = mock(Query::class.java)
    `when`(db.collection("username")).thenReturn(collection)
    `when`(collection.whereEqualTo("email", "john.doe@example.com")).thenReturn(query)
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))

    val user = userRepository.getUserByEmail("john.doe@example.com")
    assertNotNull(user)
    assertEquals("123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john.doe@example.com", user?.email)
    assertEquals(100, user?.score)
    assertEquals(listOf("friend1", "friend2"), user?.friends)
  }

  @Test
  fun getUserByEmailWithInvalidEmailReturnsNull() = runBlocking {
    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(emptyList())

    val collection = mock(CollectionReference::class.java)
    val query = mock(Query::class.java)
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.whereEqualTo("email", "invalid@example.com")).thenReturn(query)
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))

    val user = userRepository.getUserByEmail("invalid@example.com")
    assertNull(user)
  }

  @Test
  fun getUserByUserNameWithValidUserNameReturnsUser(): Unit = runBlocking {
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("username")).thenReturn("John Doe")
    `when`(document.getString("email")).thenReturn("john.doe@example.com")
    `when`(document.getLong("score")).thenReturn(100L)
    `when`(document.get("friends")).thenReturn(emptyList<String>())

    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(listOf(document))

    val query = mock(Query::class.java)
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.whereEqualTo("username", "John Doe")).thenReturn(query)
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))

    val user = userRepository.getUserByUserName("John Doe")

    assertNotNull(user)
    assertEquals("123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john.doe@example.com", user?.email)
    assertEquals(100, user?.score)
    user?.friends?.isEmpty()?.let { assertTrue(it) }
  }

  @Test
  fun getUserByUserNameWithInvalidUserNameReturnsNull(): Unit = runBlocking {
    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(emptyList())

    val query = mock(Query::class.java)
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.whereEqualTo("username", "InvalidUser")).thenReturn(query)
    `when`(query.get()).thenReturn(Tasks.forResult(querySnapshot))

    val user = userRepository.getUserByUserName("InvalidUser")

    assertNull(user)
  }

  @Test
  fun addUserWithValidUserAddsUserSuccessfully(): Unit = runBlocking {
    val user =
        User(
            uid = "123",
            username = "John Doe",
            email = "john.doe@example.com",
            score = 100,
            friends = listOf("friend1", "friend2"),
            picture = "",
            parks = emptyList())

    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document(user.uid)).thenReturn(documentRef)
    `when`(documentRef.set(user)).thenReturn(Tasks.forResult(null))

    userRepository.addUser(user)
    verify(documentRef).set(user)
  }

  @Test
  fun updateUserScoreWithValidUidUpdatesScoreSuccessfully(): Unit = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)
    `when`(documentRef.update("score", 200)).thenReturn(Tasks.forResult(null))

    userRepository.updateUserScore("123", 200)
    verify(documentRef).update("score", 200)
  }

  @Test
  fun increaseUserScoreWithValidUidAndPointsIncreasesScoreSuccessfully() = runTest {
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)
    whenever(documentRef.update(eq("score"), any())).thenReturn(Tasks.forResult(null))

    userRepository.increaseUserScore("123", 10)

    verify(documentRef).update(eq("score"), any())
  }

  @Test
  fun increaseUserScoreWithEmptyUidThrowsIllegalArgumentException() = runTest {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          runBlocking { userRepository.increaseUserScore("", 10) }
        }
    assertEquals("UID must not be empty", exception.message)
  }

  @Test
  fun increaseUserScoreWithNegativePointsThrowsIllegalArgumentException() = runTest {
    val exception =
        assertThrows(IllegalArgumentException::class.java) {
          runBlocking { userRepository.increaseUserScore("123", -10) }
        }
    assertEquals("Points must be a non-negative integer", exception.message)
  }

  @Test
  fun increaseUserScoreWithExceptionLogsError() = runTest {
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)
    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.update(eq("score"), any())).thenReturn(task)

    userRepository.increaseUserScore("123", 10)

    verify(documentRef).update(eq("score"), any())
  }

  @Test
  fun addFriendWithValidUidsAddsFriendSuccessfully(): Unit = runBlocking {
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    // Mock Firestore interactions
    whenever(db.collection("users")).thenReturn(collection)
    whenever(db.batch()).thenReturn(batch)
    whenever(collection.document("123")).thenReturn(userRef)
    whenever(collection.document("friend123")).thenReturn(friendRef)
    // Specify types in any()
    whenever(batch.update(any(), any<String>(), any<Any>())).thenReturn(batch)
    whenever(batch.commit()).thenReturn(Tasks.forResult(null))

    // Call the repository method
    userRepository.addFriend("123", "friend123")

    // Verify the interactions
    verify(batch).update(eq(userRef), eq("friends"), any<Any>())
    verify(batch).update(eq(friendRef), eq("friends"), any<Any>())
    verify(batch).commit()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun removeFriendWithValidUidsRemovesFriendSuccessfully() = runTest {
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    // Mock Firestore interactions
    whenever(db.collection("users")).thenReturn(collection)
    whenever(db.batch()).thenReturn(batch)
    whenever(collection.document("user123")).thenReturn(userRef)
    whenever(collection.document("friend123")).thenReturn(friendRef)
    // Use type-safe any() matchers
    whenever(batch.update(any<DocumentReference>(), any<String>(), any<Any>())).thenReturn(batch)

    // Use TaskCompletionSource to simulate the commit task
    val batchCommitTaskCompletionSource = TaskCompletionSource<Void>()
    batchCommitTaskCompletionSource.setResult(null) // Simulate successful completion
    val batchCommitTask = batchCommitTaskCompletionSource.task
    whenever(batch.commit()).thenReturn(batchCommitTask)

    // Call the repository method
    userRepository.removeFriend("user123", "friend123")

    // Advance coroutines until idle to ensure all coroutines have completed
    advanceUntilIdle()

    // Verify the interactions
    verify(batch).update(eq(userRef), eq("friends"), any())
    verify(batch).update(eq(friendRef), eq("friends"), any())
    verify(batch).commit()
  }

  @Test
  fun deleteUserByUidWithValidIdDeletesUserSuccessfully(): Unit = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)
    `when`(documentRef.delete()).thenReturn(Tasks.forResult(null))

    userRepository.deleteUserByUid("123")
    verify(documentRef).delete()
  }

  @Test
  fun getUserByUidWithExceptionReturnsNull() = runTest {
    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.get()).thenReturn(task)

    // Call the method under test
    val user = userRepository.getUserByUid("123")

    // Assert that the method returns null
    assertNull(user)
  }

  @Test
  fun getUserByEmailWithExceptionReturnsNull() = runTest {
    // Mock Firestore interactions to throw an exception
    val query = mock<Query>()
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.whereEqualTo("email", "test@example.com")).thenReturn(query)

    val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(query.get()).thenReturn(task)

    // Call the method under test
    val user = userRepository.getUserByEmail("test@example.com")

    // Assert that the method returns null
    assertNull(user)
  }

  @Test
  fun getFriendsByUidWithExceptionReturnsNull() = runTest {
    // Mock Firestore interactions to throw an exception when fetching user document
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("user123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.get()).thenReturn(task)

    // Call the method under test
    val friendsList = userRepository.getFriendsByUid("user123")

    // Assert that the method returns null
    assertNull(friendsList)
  }

  @Test
  fun getParksByUidWithExceptionReturnsNull() = runTest {
    // Mock Firestore interactions to throw an exception when fetching user document
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("user123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.get()).thenReturn(task)

    // Call the method under test
    val parksList = userRepository.getParksByUid("user123")

    // Assert that the method returns null
    assertNull(parksList)
  }

  @Test
  fun addUserWithExceptionLogsError() = runTest {
    // Prepare a user object
    val user =
        User(
            uid = "123",
            username = "Test User",
            email = "test@example.com",
            score = 0,
            friends = emptyList(),
            picture = "",
            parks = emptyList())

    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.set(user)).thenReturn(task)

    // Call the method under test
    userRepository.addUser(user)

    // Since the method does not return a value, we can't assert the result
    // However, we can verify that the 'set' method was called
    verify(documentRef).set(user)
  }

  @Test
  fun updateUserScoreWithExceptionLogsError() = runTest {
    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.update("score", 100)).thenReturn(task)

    // Call the method under test
    userRepository.updateUserScore("123", 100)

    // Verify that 'update' was called
    verify(documentRef).update("score", 100)
  }

  @Test
  fun addFriendWithExceptionLogsError() = runTest {
    // Mock Firestore interactions to throw an exception during batch commit
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    whenever(db.collection("users")).thenReturn(collection)
    whenever(db.batch()).thenReturn(batch)
    whenever(collection.document("user123")).thenReturn(userRef)
    whenever(collection.document("friend123")).thenReturn(friendRef)
    whenever(batch.update(any<DocumentReference>(), any<String>(), any<Any>())).thenReturn(batch)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(batch.commit()).thenReturn(task)

    // Call the method under test
    userRepository.addFriend("user123", "friend123")

    // Verify that 'update' and 'commit' were called
    verify(batch, times(2)).update(any<DocumentReference>(), any<String>(), any<Any>())
    verify(batch).commit()
  }

  @Test
  fun removeFriendWithExceptionLogsError() = runTest {
    // Mock Firestore interactions to throw an exception during batch commit
    val userRef = mock<DocumentReference>()
    val friendRef = mock<DocumentReference>()

    whenever(db.collection("users")).thenReturn(collection)
    whenever(db.batch()).thenReturn(batch)
    whenever(collection.document("user123")).thenReturn(userRef)
    whenever(collection.document("friend123")).thenReturn(friendRef)
    whenever(batch.update(any<DocumentReference>(), any<String>(), any<Any>())).thenReturn(batch)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(batch.commit()).thenReturn(task)

    // Call the method under test
    userRepository.removeFriend("user123", "friend123")

    // Verify that 'update' and 'commit' were called
    verify(batch, times(2)).update(any<DocumentReference>(), any<String>(), any<Any>())
    verify(batch).commit()
  }

  @Test
  fun addNewParkWithExceptionLogsError() = runTest {
    // Mock Firestore interactions to throw an exception during batch commit
    val userRef = mock<DocumentReference>()
    val parkId = "park123"

    whenever(db.collection("users")).thenReturn(collection)
    whenever(db.batch()).thenReturn(batch)
    whenever(collection.document("user123")).thenReturn(userRef)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(batch.commit()).thenReturn(task)

    // Call the method under test
    userRepository.addNewPark("user123", parkId)

    // Verify that 'update' and 'commit' were called
    verify(userRef, times(1)).update(any<String>(), any<Any>())
  }

  @Test
  fun deleteUserByUidWithExceptionLogsError() = runTest {
    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.delete()).thenReturn(task)

    // Call the method under test
    userRepository.deleteUserByUid("123")

    // Verify that 'delete' was called
    verify(documentRef).delete()
  }

  @Test
  fun documentToUserWithValidDocumentReturnsUser() {
    // Mock the DocumentSnapshot
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))
    whenever(document.get("parks")).thenReturn(listOf("park1", "park2"))

    // Call the function
    val user = userRepository.documentToUser(document)

    // Assert the result
    assertNotNull(user)
    assertEquals("user123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john@example.com", user?.email)
    assertEquals(100, user?.score)
    assertEquals(listOf("friend1", "friend2"), user?.friends)
    assertEquals(listOf("park1", "park2"), user?.parks)
  }

  @Test
  fun documentToUserWithMissingNameReturnsNull() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn(null) // Missing name
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNull(user)
  }

  @Test
  fun documentToUserWithMissingEmailReturnsNull() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn(null) // Missing email
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNull(user)
  }

  @Test
  fun documentToUserWithMissingScoreSetsScoreToZero() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(null) // Missing score
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertEquals(0, user?.score) // Score should default to 0
  }

  @Test
  fun documentToUserWithMissingFriendsSetsEmptyFriendsList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(null) // Missing friends

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun documentToUserWithFriendsOfIncorrectTypeSetsEmptyFriendsList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn("not a list") // Incorrect type

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun documentToUserWithExceptionWhenGettingFriendsSetsEmptyFriendsList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    // Simulate exception when accessing 'friends' field
    whenever(document.get("friends")).thenThrow(RuntimeException("Test exception"))

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun documentToUserWithMissingParksSetsEmptyParksList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("parks")).thenReturn(null) // Missing Parks

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.parks?.isEmpty() == true) // parks list should be empty
  }

  @Test
  fun documentToUserWithParksOfIncorrectTypeSetsEmptyParksList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn("not a list") // Incorrect type

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.parks?.isEmpty() == true) // parks list should be empty
  }

  @Test
  fun documentToUserWithExceptionWhenGettingParksSetsEmptyParksList() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("username")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    // Simulate exception when accessing 'friends' field
    whenever(document.get("parks")).thenThrow(RuntimeException("Test exception"))

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.parks?.isEmpty() == true) // parks list should be empty
  }

  @Test
  fun documentToUserWithExceptionWhenGettingFieldsReturnsNull() {
    whenever(document.id).thenReturn("user123")
    // Simulate exception when accessing 'name' field
    whenever(document.getString("username")).thenThrow(RuntimeException("Test exception"))

    val user = userRepository.documentToUser(document)

    assertNull(user) // Should return null due to exception
  }

  @Test
  fun getOrAddUserByUidWithValidUidReturnsUser() = runTest {
    // Setup the DocumentSnapshot
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("username")).thenReturn("John Doe")
    `when`(document.getString("email")).thenReturn("john.doe@example.com")
    `when`(document.getLong("score")).thenReturn(100L)
    `when`(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    // Mock Firestore interactions
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)

    // Use TaskCompletionSource to create a controllable Task
    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    // Call the repository method
    val user =
        userRepository.getOrAddUserByUid(
            "123",
            User(
                "123",
                "John Doe",
                "john.doe@example.com",
                100,
                listOf("friend1", "friend2"),
                picture = "",
                parks = emptyList()))

    // Assert the result is not null and contains expected values
    assertNotNull(user)
    assertEquals("123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john.doe@example.com", user?.email)
    assertEquals(100, user?.score)
    assertEquals(listOf("friend1", "friend2"), user?.friends)
  }

  @Test
  fun getOrAddUserByUidWithInvalidUidCreateUser() = runTest {
    // Setup the DocumentSnapshot to simulate non-existence
    `when`(document.exists()).thenReturn(false)

    // Mock Firestore interactions
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("invalid")).thenReturn(documentRef)

    // Use TaskCompletionSource to create a controllable Task for non-existing user
    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setResult(document)
    val task = taskCompletionSource.task
    `when`(documentRef.get()).thenReturn(task)

    // Mock the set operation to add a new user
    `when`(documentRef.set(any(User::class.java))).thenReturn(Tasks.forResult(null))

    // Call the repository method
    val newUser =
        User(
            "invalid",
            "New User",
            "new.user@example.com",
            0,
            emptyList(),
            picture = "",
            parks = emptyList())
    val user = userRepository.getOrAddUserByUid("invalid", newUser)

    // Assert the result is not null and contains expected values
    assertNotNull(user)
    assertEquals("invalid", user?.uid)
    assertEquals("New User", user?.username)
    assertEquals("new.user@example.com", user?.email)
    assertEquals(0, user?.score)
    assertTrue(user?.friends?.isEmpty() == true)

    // Verify that the set method was called to create a new user
    verify(documentRef).set(newUser)
  }

  @Test
  fun getOrAddUserByUidWithExceptionReturnsNull() = runTest {
    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<DocumentSnapshot>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.get()).thenReturn(task)

    // Call the method under test
    val user =
        userRepository.getOrAddUserByUid(
            "123",
            User(
                "123",
                "John Doe",
                "john.doe@example.com",
                100,
                listOf("friend1", "friend2"),
                picture = "",
                parks = emptyList()))

    // Assert that the method returns null
    assertNull(user)
  }

  @Test
  fun getUsersByUidsWithValidUidsReturnsListOfUsers() = runTest {
    `when`(db.collection(any())).thenReturn(collection)
    `when`(collection.document(any())).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forResult(document))

    `when`(document.exists()).thenReturn(true)

    userRepository.getUsersByUids(listOf("123", "456"))

    verify(documentRef, times(2)).get()
  }
}
