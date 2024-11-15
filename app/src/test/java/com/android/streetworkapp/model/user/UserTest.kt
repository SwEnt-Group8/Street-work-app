package com.android.streetworkapp.model.user

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserTest {

  @Test
  fun userInitialization_withValidData() {
    val user =
        User(
            uid = "123",
            username = "John Doe",
            email = "john.doe@example.com",
            score = 100,
            friends = listOf("456", "789"),
            picture = "")
    assertEquals("123", user.uid)
    assertEquals("John Doe", user.username)
    assertEquals("john.doe@example.com", user.email)
    assertEquals(100, user.score)
    assertEquals(listOf("456", "789"), user.friends)
  }

  @Test
  fun userInitialization_withEmptyFriendsList() {
    val user =
        User(
            uid = "123",
            username = "John Doe",
            email = "john.doe@example.com",
            score = 100,
            friends = emptyList(),
            picture = "")
    assertEquals("123", user.uid)
    assertEquals("John Doe", user.username)
    assertEquals("john.doe@example.com", user.email)
    assertEquals(100, user.score)
    assertTrue(user.friends.isEmpty())
  }
}
