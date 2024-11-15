package com.android.streetworkapp.model.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class UserViewModelTest {

  private lateinit var repository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private val testDispatcher = StandardTestDispatcher()
  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock()
    userViewModel = UserViewModel(repository)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain() // Reset the Main dispatcher after the test
  }

  @Test
  fun getNewUidCallsRepositoryAndReturnsUid() {
    whenever(repository.getNewUid()).thenReturn("uniqueId")
    val uid = userViewModel.getNewUid()
    assertEquals("uniqueId", uid)
    verify(repository).getNewUid()
  }

  @Test
  fun getUserByIdCallsRepositoryWithCorrectUid() = runTest {
    val uid = "user123"
    val user = User(uid, "John Doe", "john@example.com", 100, emptyList(), picture = "")
    whenever(repository.getUserByUid(uid)).thenReturn(user)
    userViewModel.getUserByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByUid(uid)
  }

  @Test
  fun getUserByEmailCallsRepositoryWithCorrectEmail() = runTest {
    val email = "john@example.com"
    val user = User("user123", "John Doe", email, 100, emptyList(), picture = "")
    whenever(repository.getUserByEmail(email)).thenReturn(user)
    userViewModel.getUserByEmail(email)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByEmail(email)
  }

  @Test
  fun getUserByUserNameCallsRepositoryWithCorrectUsername() = runTest {
    val userName = "John Doe"
    val user = User("user123", userName, "john@example.com", 0, emptyList(), picture = "")
    whenever(repository.getUserByUserName(userName)).thenReturn(user)
    userViewModel.getUserByUserName(userName)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByUserName(userName)
  }

  @Test
  fun getFriendsByUidCallsRepositoryWithCorrectUid() = runTest {
    val uid = "user123"
    val friends =
        listOf(
            User("friend1", "Friend One", "friend1@example.com", 50, emptyList(), picture = ""),
            User("friend2", "Friend Two", "friend2@example.com", 60, emptyList(), picture = ""))
    whenever(repository.getFriendsByUid(uid)).thenReturn(friends)
    userViewModel.getFriendsByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getFriendsByUid(uid)
  }

  @Test
  fun addUserCallsRepositoryWithCorrectUser() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList(), picture = "")
    userViewModel.addUser(user)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addUser(user)
  }

  @Test
  fun updateUserScoreCallsRepositoryWithCorrectUidAndScoreAndIncrement() = runTest {
    val uid = "user123"
    val newScore = 200
    val incrScore = 50
    userViewModel.updateUserScore(uid, newScore)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateUserScore(uid, newScore)
    userViewModel.increaseUserScore(uid, incrScore)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).increaseUserScore(uid, incrScore)
  }

  @Test
  fun addFriendCallsRepositoryWithCorrectUids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.addFriend(uid, friendUid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addFriend(uid, friendUid)
  }

  @Test
  fun removeFriendCallsRepositoryWithCorrectUids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.removeFriend(uid, friendUid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).removeFriend(uid, friendUid)
  }

  @Test
  fun deleteUserByUidCallsRepositoryWithCorrectId() = runTest {
    val uid = "user123"
    userViewModel.deleteUserByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteUserByUid(uid)
  }

  @Test
  fun loadCurrentUserCallsRepositoryWithCorrectUidAndUpdatesCurrentUser() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList(), picture = "")
    userViewModel.loadCurrentUser(user)
    testDispatcher.scheduler.advanceUntilIdle()
    val observedUser = userViewModel.currentUser.first()
    assertEquals(user, observedUser)
  }

  @Test
  fun setCurrentUserUpdatesCurrentUser() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList(), picture = "")
    userViewModel.setCurrentUser(user)
    val observedUser = userViewModel.currentUser.first()
    assertEquals(user, observedUser)
  }

  @Test
  fun getUserUpdatesUser() = runTest {
    val user = User("user123", "Jane Doe", "jane@example.com", 50, emptyList(), picture = "")
    whenever(repository.getUserByEmail("jane@example.com")).thenReturn(user)
    userViewModel.getUserByEmail("jane@example.com")
    testDispatcher.scheduler.advanceUntilIdle()
    val observedUser = userViewModel.user.first()
    verify(repository).getUserByEmail("jane@example.com")
    assertEquals(user, observedUser)
  }

  @Test
  fun getFriendsByUidUpdatesFriends() = runTest {
    val uid = "user123"
    val friends =
        listOf(
            User("friend1", "Friend One", "friend1@example.com", 50, emptyList(), picture = ""),
            User("friend2", "Friend Two", "friend2@example.com", 60, emptyList(), picture = ""))
    whenever(repository.getFriendsByUid(uid)).thenReturn(friends)
    userViewModel.getFriendsByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    val observedFriends = userViewModel.friends.first()
    verify(repository).getFriendsByUid(uid)
    assertEquals(friends, observedFriends)
  }

  @Test
  fun getOrAddUserByUidCallsRepositoryWithCorrectUidAndUser() = runTest {
    val uid = "user123"
    val user = User(uid, "John Doe", "john@example.com", 100, emptyList(), picture = "")
    whenever(repository.getOrAddUserByUid(uid, user)).thenReturn(user)
    userViewModel.getOrAddUserByUid(uid, user)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getOrAddUserByUid(uid, user)
  }

  @Test
  fun getOrAddUserByUidUpdateUser() = runTest {
    val uid = "user123"
    val user = User(uid, "John Doe", "john@example.com", 100, emptyList(), picture = "")
    whenever(repository.getOrAddUserByUid(uid, user)).thenReturn(user)
    userViewModel.getOrAddUserByUid(uid, user)
    testDispatcher.scheduler.advanceUntilIdle()
    val observedUser = userViewModel.user.first()
    assertEquals(user, observedUser)
  }
}
