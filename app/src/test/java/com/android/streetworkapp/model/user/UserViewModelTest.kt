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
  fun getNewUid_calls_repository_and_returns_uid() {
    whenever(repository.getNewUid()).thenReturn("uniqueId")
    val uid = userViewModel.getNewUid()
    assertEquals("uniqueId", uid)
    verify(repository).getNewUid()
  }

  @Test
  fun getUserById_calls_repository_with_correct_uid() = runTest {
    val uid = "user123"
    val user = User(uid, "John Doe", "john@example.com", 100, emptyList())
    whenever(repository.getUserByUid(uid)).thenReturn(user)
    userViewModel.getUserByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByUid(uid)
  }

  @Test
  fun getUserByEmail_calls_repository_with_correct_email() = runTest {
    val email = "john@example.com"
    val user = User("user123", "John Doe", email, 100, emptyList())
    whenever(repository.getUserByEmail(email)).thenReturn(user)
    userViewModel.getUserByEmail(email)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByEmail(email)
  }

  @Test
  fun getUserByUserName_calls_repository_with_correct_username() = runTest {
    val userName = "John Doe"
    val user = User("user123", userName, "john@example.com", 0, emptyList())
    whenever(repository.getUserByUserName(userName)).thenReturn(user)
    userViewModel.getUserByUserName(userName)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getUserByUserName(userName)
  }

  @Test
  fun getFriendsByUid_calls_repository_with_correct_uid() = runTest {
    val uid = "user123"
    val friends =
        listOf(
            User("friend1", "Friend One", "friend1@example.com", 50, emptyList()),
            User("friend2", "Friend Two", "friend2@example.com", 60, emptyList()))
    whenever(repository.getFriendsByUid(uid)).thenReturn(friends)
    userViewModel.getFriendsByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getFriendsByUid(uid)
  }

  @Test
  fun addUser_calls_repository_with_correct_user() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList())
    userViewModel.addUser(user)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addUser(user)
  }

  @Test
  fun updateUserScore_calls_repository_with_correct_uid_and_score_and_increment() = runTest {
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
  fun addFriend_calls_repository_with_correct_uids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.addFriend(uid, friendUid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addFriend(uid, friendUid)
  }

  @Test
  fun removeFriend_calls_repository_with_correct_uids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.removeFriend(uid, friendUid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).removeFriend(uid, friendUid)
  }

  @Test
  fun deleteUserByUid_calls_repository_with_correct_id() = runTest {
    val uid = "user123"
    userViewModel.deleteUserByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteUserByUid(uid)
  }

  @Test
  fun loadCurrentUser_calls_repository_with_correct_uid_and_updates_currentUser() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList())
    userViewModel.loadCurrentUser(user)
    testDispatcher.scheduler.advanceUntilIdle()
    val observedUser = userViewModel.currentUser.first()
    assertEquals(user, observedUser)
  }

  @Test
  fun setCurrentUser_updates_currentUser() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList())
    userViewModel.setCurrentUser(user)
    val observedUser = userViewModel.currentUser.first()
    assertEquals(user, observedUser)
  }

  @Test
  fun getUser_updates_user() = runTest {
    val user = User("user123", "Jane Doe", "jane@example.com", 50, emptyList())
    whenever(repository.getUserByEmail("jane@example.com")).thenReturn(user)
    userViewModel.getUserByEmail("jane@example.com")
    testDispatcher.scheduler.advanceUntilIdle()
    val observedUser = userViewModel.user.first()
    verify(repository).getUserByEmail("jane@example.com")
    assertEquals(user, observedUser)
  }

  @Test
  fun getFriendsByUid_updates_friends() = runTest {
    val uid = "user123"
    val friends =
        listOf(
            User("friend1", "Friend One", "friend1@example.com", 50, emptyList()),
            User("friend2", "Friend Two", "friend2@example.com", 60, emptyList()))
    whenever(repository.getFriendsByUid(uid)).thenReturn(friends)
    userViewModel.getFriendsByUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    val observedFriends = userViewModel.friends.first()
    verify(repository).getFriendsByUid(uid)
    assertEquals(friends, observedFriends)
  }
}
