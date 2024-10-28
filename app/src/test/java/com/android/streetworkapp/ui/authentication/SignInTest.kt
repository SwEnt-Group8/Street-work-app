package com.android.streetworkapp.ui.authentication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class SignInTest {

  @get:Rule val mockitoRule: MockitoRule = MockitoJUnit.rule()
  @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var userViewModel: UserViewModel
  @Mock lateinit var repository: UserRepository
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    userViewModel = UserViewModel(repository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun checkAndAddUser_withExistingUser_doesNotAddUser() = runTest {
    // Given a FirebaseUser
    val firebaseUser =
        mock(FirebaseUser::class.java).apply {
          `when`(uid).thenReturn("user123")
          `when`(displayName).thenReturn("John Doe")
          `when`(email).thenReturn("john@example.com")
        }

    // Mock the repository to return an existing user (user already exists)
    val existingUser = User("user123", "John Doe", "john@example.com", 100, emptyList())
    `when`(repository.getUserByUid("user123")).thenReturn(existingUser)

    // Call the function
    checkAndAddUser(firebaseUser, userViewModel)

    // Advance the test dispatcher to execute the coroutine
    testDispatcher.scheduler.advanceUntilIdle()

    // Verify that addUser is not called
    verify(repository, never()).addUser(any())
  }

  @Test
  fun checkAndAddUser_withNullUser_doesNothing() = runTest {
    // Call the function with a null user
    checkAndAddUser(null, userViewModel)

    // Verify that getUserByUid and addUser are not called
    verify(repository, never()).getUserByUid(any())
    verify(repository, never()).addUser(any())
  }
}
