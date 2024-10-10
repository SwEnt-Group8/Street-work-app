package com.android.streetworkapp.auth

import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class GoogleAuthServiceTest {
  private lateinit var googleAuthService: GoogleAuthService

  @Mock private lateinit var mockAuth: FirebaseAuth

  @Mock private lateinit var mockUser: FirebaseUser

  private val token = "dummy_token"

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    googleAuthService = GoogleAuthService(token, mockAuth)
  }

  @Test
  fun testSignOutCallsFirebaseAuthSignOut() {
    // Act
    googleAuthService.signOut()

    // Assert
    verify(mockAuth).signOut() // Verify that signOut() was called
  }

  @Test
  fun testGetCurrentUserReturnsCurrentUser() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(mockUser)

    // Act
    val currentUser = googleAuthService.getCurrentUser()

    // Assert
    assertEquals(mockUser, currentUser) // Check that the returned user is the same as the mock user
  }

  @Test
  fun testGetCurrentUserReturnsNullWhenNuserIsSignedIn() {
    // Arrange
    `when`(mockAuth.currentUser).thenReturn(null)

    // Act
    val currentUser = googleAuthService.getCurrentUser()

    // Assert
    assertNull(currentUser) // Check that the returned user is null
  }
}
