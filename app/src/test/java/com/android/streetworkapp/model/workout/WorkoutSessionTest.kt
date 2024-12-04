package com.android.streetworkapp.model.workout

import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WorkoutSessionTest {

  private lateinit var workoutSession: WorkoutSession

  @Before
  fun setup() {
    TimeZone.setDefault(
        TimeZone.getTimeZone("UTC")) // The timezone is assumed to be in UTC in those tests
    // Initialize a WorkoutSession object with epoch timestamps
    workoutSession =
        WorkoutSession(
            sessionId = "123",
            startTime = 1732430400000L,
            endTime = 1732434000000L,
            sessionType = SessionType.SOLO)
  }

  @Test
  fun `test getFormattedStartTime returns correctly formatted start time`() {
    val expectedFormat = "24/11/2024 06:40"
    val actualFormat = workoutSession.getFormattedStartTime()
    assertEquals(expectedFormat, actualFormat)
  }

  @Test
  fun `test getFormattedEndTime returns correctly formatted end time`() {
    val expectedFormat = "24/11/2024 07:40"
    val actualFormat = workoutSession.getFormattedEndTime()
    assertEquals(expectedFormat, actualFormat)
  }
}
