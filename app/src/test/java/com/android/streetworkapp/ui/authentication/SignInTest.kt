package com.android.streetworkapp.ui.authentication

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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SignInTest {

  @get:Rule
  val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock
  lateinit var userViewModel: UserViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun checkAndAddUser_withValidUser_addsUserIfNotExists() = runTest {
    // Given a FirebaseUser
    val firebaseUser = mock(FirebaseUser::class.java)
    `when`(firebaseUser.uid).thenReturn("user123")
    `when`(firebaseUser.displayName).thenReturn("John Doe")
    `when`(firebaseUser.email).thenReturn("john@example.com")

    // User doesn't exist in the system
    whenever(userViewModel.getUserByUid("user123")).thenReturn(null)

    // Call the function
    checkAndAddUser(firebaseUser, userViewModel)
    // Advance the time
    testDispatcher.scheduler.advanceUntilIdle()
    // Verify that addUser is called
    verify(userViewModel).addUser(any())
  }
}