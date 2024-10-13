package com.android.streetworkapp.model.user

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class UserViewModelTest {

  private lateinit var repository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @Before
  fun setUp() {
    repository = mock()
    userViewModel = UserViewModel(repository)
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
    userViewModel.getUserById(uid)
    verify(repository).getUserByUid(uid)
  }

  @Test
  fun getUserByEmail_calls_repository_with_correct_email() = runTest {
    val email = "john@example.com"
    val user = User("user123", "John Doe", email, 100, emptyList())
    whenever(repository.getUserByEmail(email)).thenReturn(user)
    userViewModel.getUserByEmail(email)
    verify(repository).getUserByEmail(email)
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
    verify(repository).getFriendsByUid(uid)
  }

  @Test
  fun addUser_calls_repository_with_correct_user() = runTest {
    val user = User("user123", "John Doe", "john@example.com", 100, emptyList())
    userViewModel.addUser(user)
    verify(repository).addUser(user)
  }

  @Test
  fun updateUserScore_calls_repository_with_correct_uid_and_score() = runTest {
    val uid = "user123"
    val newScore = 200
    userViewModel.updateUserScore(uid, newScore)
    verify(repository).updateUserScore(uid, newScore)
  }

  @Test
  fun addFriend_calls_repository_with_correct_uids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.addFriend(uid, friendUid)
    verify(repository).addFriend(uid, friendUid)
  }

  @Test
  fun removeFriend_calls_repository_with_correct_uids() = runTest {
    val uid = "user123"
    val friendUid = "friend123"
    userViewModel.removeFriend(uid, friendUid)
    verify(repository).removeFriend(uid, friendUid)
  }

  @Test
  fun deleteUserById_calls_repository_with_correct_id() = runTest {
    val uid = "user123"
    userViewModel.deleteUserById(uid)
    verify(repository).deleteUserById(uid)
  }
}
