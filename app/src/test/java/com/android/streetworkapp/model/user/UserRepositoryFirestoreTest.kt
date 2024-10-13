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
  }

  @Test
  fun getNewUid_returnsUniqueId() {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document()).thenReturn(documentRef)
    `when`(documentRef.id).thenReturn("uniqueId")

    val uid = userRepository.getNewUid()
    assertEquals("uniqueId", uid)
  }

  @Test
  fun getUserByUid_withValidUid_returnsUser() = runTest {
    // Setup the DocumentSnapshot
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("name")).thenReturn("John Doe")
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
  fun getUserByUid_withInvalidUid_returnsNull() = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("invalid")).thenReturn(documentRef)
    `when`(documentRef.get()).thenReturn(Tasks.forException(Exception("User not found")))

    val user = userRepository.getUserByUid("invalid")
    assertNull(user)
  }

  @Test
  fun getUserByEmail_withValidEmail_returnsUser() = runBlocking {
    // Setup the DocumentSnapshot
    `when`(document.exists()).thenReturn(true)
    `when`(document.id).thenReturn("123")
    `when`(document.getString("name")).thenReturn("John Doe")
    `when`(document.getString("email")).thenReturn("john.doe@example.com")
    `when`(document.getLong("score")).thenReturn(100L)
    `when`(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val querySnapshot = mock(QuerySnapshot::class.java)
    `when`(querySnapshot.documents).thenReturn(listOf(document))

    val query = mock(Query::class.java)
    `when`(db.collection("users")).thenReturn(collection)
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
  fun getFriendsByUid_withValidUid_returnsListOfFriends() = runTest {
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
    whenever(friendDocument1.getString("name")).thenReturn("Friend One")
    whenever(friendDocument1.getString("email")).thenReturn("friend1@example.com")
    whenever(friendDocument1.getLong("score")).thenReturn(50L)
    whenever(friendDocument1.get("friends")).thenReturn(emptyList<String>())

    val friendDocument2 = mock<DocumentSnapshot>()
    whenever(friendDocument2.exists()).thenReturn(true)
    whenever(friendDocument2.id).thenReturn("friend2")
    whenever(friendDocument2.getString("name")).thenReturn("Friend Two")
    whenever(friendDocument2.getString("email")).thenReturn("friend2@example.com")
    whenever(friendDocument2.getLong("score")).thenReturn(60L)
    whenever(friendDocument2.get("friends")).thenReturn(emptyList<String>())

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

    assertNotNull(friend2)
    assertEquals("Friend Two", friend2?.username)
    assertEquals("friend2@example.com", friend2?.email)
    assertEquals(60, friend2?.score)
    assertTrue(friend2?.friends?.isEmpty() == true)
  }

  @Test
  fun getUserByEmail_withInvalidEmail_returnsNull() = runBlocking {
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
  fun addUser_withValidUser_addsUserSuccessfully(): Unit = runBlocking {
    val user =
        User(
            uid = "123",
            username = "John Doe",
            email = "john.doe@example.com",
            score = 100,
            friends = listOf("friend1", "friend2"))

    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document(user.uid)).thenReturn(documentRef)
    `when`(documentRef.set(user)).thenReturn(Tasks.forResult(null))

    userRepository.addUser(user)
    verify(documentRef).set(user)
  }

  @Test
  fun updateUserScore_withValidUid_updatesScoreSuccessfully(): Unit = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)
    `when`(documentRef.update("score", 200)).thenReturn(Tasks.forResult(null))

    userRepository.updateUserScore("123", 200)
    verify(documentRef).update("score", 200)
  }

  @Test
  fun addFriend_withValidUids_addsFriendSuccessfully(): Unit = runBlocking {
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

  @Test
  fun removeFriend_withValidUids_removesFriendSuccessfully() = runTest {
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
  fun deleteUserById_withValidId_deletesUserSuccessfully(): Unit = runBlocking {
    `when`(db.collection("users")).thenReturn(collection)
    `when`(collection.document("123")).thenReturn(documentRef)
    `when`(documentRef.delete()).thenReturn(Tasks.forResult(null))

    userRepository.deleteUserById("123")
    verify(documentRef).delete()
  }

  @Test
  fun getUserByUid_withException_returnsNull() = runTest {
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
  fun getUserByEmail_withException_returnsNull() = runTest {
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
  fun getFriendsByUid_withException_returnsNull() = runTest {
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
  fun addUser_withException_logsError() = runTest {
    // Prepare a user object
    val user =
        User(
            uid = "123",
            username = "Test User",
            email = "test@example.com",
            score = 0,
            friends = emptyList())

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
  fun updateUserScore_withException_logsError() = runTest {
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
  fun addFriend_withException_logsError() = runTest {
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
  fun removeFriend_withException_logsError() = runTest {
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
  fun deleteUserById_withException_logsError() = runTest {
    // Mock Firestore interactions to throw an exception
    whenever(db.collection("users")).thenReturn(collection)
    whenever(collection.document("123")).thenReturn(documentRef)

    val taskCompletionSource = TaskCompletionSource<Void>()
    taskCompletionSource.setException(Exception("Firestore exception"))
    val task = taskCompletionSource.task
    whenever(documentRef.delete()).thenReturn(task)

    // Call the method under test
    userRepository.deleteUserById("123")

    // Verify that 'delete' was called
    verify(documentRef).delete()
  }

  @Test
  fun `documentToUser with valid document returns User`() {
    // Mock the DocumentSnapshot
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    // Call the function
    val user = userRepository.documentToUser(document)

    // Assert the result
    assertNotNull(user)
    assertEquals("user123", user?.uid)
    assertEquals("John Doe", user?.username)
    assertEquals("john@example.com", user?.email)
    assertEquals(100, user?.score)
    assertEquals(listOf("friend1", "friend2"), user?.friends)
  }

  @Test
  fun `documentToUser with missing name returns null`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn(null) // Missing name
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNull(user)
  }

  @Test
  fun `documentToUser with missing email returns null`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn(null) // Missing email
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNull(user)
  }

  @Test
  fun `documentToUser with missing score sets score to zero`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(null) // Missing score
    whenever(document.get("friends")).thenReturn(listOf("friend1", "friend2"))

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertEquals(0, user?.score) // Score should default to 0
  }

  @Test
  fun `documentToUser with missing friends sets empty friends list`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn(null) // Missing friends

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun `documentToUser with friends of incorrect type sets empty friends list`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    whenever(document.get("friends")).thenReturn("not a list") // Incorrect type

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun `documentToUser with exception when getting friends sets empty friends list`() {
    whenever(document.id).thenReturn("user123")
    whenever(document.getString("name")).thenReturn("John Doe")
    whenever(document.getString("email")).thenReturn("john@example.com")
    whenever(document.getLong("score")).thenReturn(100L)
    // Simulate exception when accessing 'friends' field
    whenever(document.get("friends")).thenThrow(RuntimeException("Test exception"))

    val user = userRepository.documentToUser(document)

    assertNotNull(user)
    assertTrue(user?.friends?.isEmpty() == true) // Friends list should be empty
  }

  @Test
  fun `documentToUser with exception when getting fields returns null`() {
    whenever(document.id).thenReturn("user123")
    // Simulate exception when accessing 'name' field
    whenever(document.getString("name")).thenThrow(RuntimeException("Test exception"))

    val user = userRepository.documentToUser(document)

    assertNull(user) // Should return null due to exception
  }
}
