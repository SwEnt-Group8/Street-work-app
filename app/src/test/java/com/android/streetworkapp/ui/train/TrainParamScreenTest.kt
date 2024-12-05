package com.android.streetworkapp.ui.train

import org.junit.Assert.assertEquals
import org.junit.Test

class TrainParamScreenTest {

  @Test
  fun `handleDeleteInput should correctly delete last digit`() {
    var updatedMinutes = 0
    var updatedSeconds = 0

    handleDeleteInput(
        minutes = 12,
        seconds = 34,
        onUpdateMinutes = { updatedMinutes = it },
        onUpdateSeconds = { updatedSeconds = it })

    // Expected behavior: "1234" -> "0123"
    assertEquals(1, updatedMinutes)
    assertEquals(23, updatedSeconds)
  }

  @Test
  fun `handleDeleteInput should handle single digit times`() {
    var updatedMinutes = 0
    var updatedSeconds = 0

    handleDeleteInput(
        minutes = 0,
        seconds = 5,
        onUpdateMinutes = { updatedMinutes = it },
        onUpdateSeconds = { updatedSeconds = it })

    // Expected behavior: "0005" -> "0000"
    assertEquals(0, updatedMinutes)
    assertEquals(0, updatedSeconds)
  }

  @Test
  fun `handleInput should correctly add single digit input`() {
    var updatedMinutes = 0
    var updatedSeconds = 0

    handleInput(
        input = "5",
        minutes = 1,
        seconds = 23,
        onUpdateMinutes = { updatedMinutes = it },
        onUpdateSeconds = { updatedSeconds = it })

    // Expected behavior: "0123" + "5" -> "1235"
    assertEquals(12, updatedMinutes)
    assertEquals(35, updatedSeconds)
  }

  @Test
  fun `handleInput should handle input of 00 correctly`() {
    var updatedMinutes = 0
    var updatedSeconds = 0

    handleInput(
        input = "00",
        minutes = 0,
        seconds = 12,
        onUpdateMinutes = { updatedMinutes = it },
        onUpdateSeconds = { updatedSeconds = it })

    // Expected behavior: "0012" + "00" -> "1200"
    assertEquals(12, updatedMinutes)
    assertEquals(0, updatedSeconds)
  }
}
