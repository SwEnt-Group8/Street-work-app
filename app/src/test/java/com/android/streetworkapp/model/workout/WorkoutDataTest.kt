package com.android.streetworkapp.model.workout

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutDataTest {

  @Test
  fun `test WorkoutData default values`() {
    // Create an instance of WorkoutData with default values
    val workoutData = WorkoutData()

    // Assert default values
    assertEquals("", workoutData.userUid)
    assertTrue(workoutData.workoutSessions.isEmpty())
  }

  @Test
  fun `test WorkoutData with custom values`() {
    // Prepare custom values
    val workoutSessions =
        listOf(
            WorkoutSession("session1", 0L, 0L, SessionType.ALONE),
            WorkoutSession("session2", 0L, 0L, SessionType.ALONE))
    val userUid = "test_user"

    // Create an instance of WorkoutData with custom values
    val workoutData = WorkoutData(userUid = userUid, workoutSessions = workoutSessions)

    // Assert custom values
    assertEquals("test_user", workoutData.userUid)
    assertEquals(2, workoutData.workoutSessions.size)
    assertEquals("session1", workoutData.workoutSessions[0].sessionId)
  }
}
