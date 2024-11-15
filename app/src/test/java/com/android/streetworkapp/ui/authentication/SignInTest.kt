package com.android.streetworkapp.ui.authentication

import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SignInTest {

  private lateinit var firebaseUser: FirebaseUser

  @Before
  fun setUp() {
    firebaseUser = Mockito.mock(FirebaseUser::class.java)
  }

  @Test
  fun testCreateUserFromFirebaseUser() {
    // Mock the FirebaseUser properties
    Mockito.`when`(firebaseUser.uid).thenReturn("testUid")
    Mockito.`when`(firebaseUser.displayName).thenReturn("Test User")
    Mockito.`when`(firebaseUser.email).thenReturn("testuser@example.com")

    // Call the method to test
    val user = createNewUserFromFirebaseUser(firebaseUser)

    // Verify the results
    assertEquals("testUid", user.uid)
    assertEquals("Test User", user.username)
    assertEquals("testuser@example.com", user.email)
    assertEquals(0, user.score)
    assertEquals(emptyList<String>(), user.friends)
  }

  @Test
  fun testCreateUserFromFirebaseUserWithEmptyUid() {
    // Mock the FirebaseUser properties with an empty UID
    Mockito.`when`(firebaseUser.uid).thenReturn("")
    Mockito.`when`(firebaseUser.displayName).thenReturn("Test User")
    Mockito.`when`(firebaseUser.email).thenReturn("testuser@example.com")

    // Verify that an exception is thrown
    assertThrows(IllegalArgumentException::class.java) {
      createNewUserFromFirebaseUser(firebaseUser)
    }
  }
}
