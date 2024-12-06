package com.android.streetworkapp.ui.train

import com.android.streetworkapp.model.workout.Exercise
import com.android.streetworkapp.model.workout.SessionType
import com.android.streetworkapp.model.workout.WorkoutViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class TrainSoloScreenTest {

  private lateinit var workoutViewModel: WorkoutViewModel

  @Before
  fun setUp() {
    // Mock the WorkoutViewModel
    workoutViewModel = mock()
  }

  @Test
  fun `should not add exercise when userId is null`() {
    // Call the function with null userId
    addExerciseToWorkout(
        userId = null,
        workoutViewModel = workoutViewModel,
        activity = "Push-ups",
        duration = 10,
        reps = 15,
        sets = 3)

    // Verify that getOrAddExerciseToWorkout is not called
    verifyNoInteractions(workoutViewModel)
  }

  @Test
  fun `should not add exercise when userId is empty`() {
    // Call the function with empty userId
    addExerciseToWorkout(
        userId = "",
        workoutViewModel = workoutViewModel,
        activity = "Push-ups",
        duration = 10,
        reps = 15,
        sets = 3)

    // Verify that getOrAddExerciseToWorkout is not called
    verifyNoInteractions(workoutViewModel)
  }

  @Test
  fun `should add exercise to workout when userId is valid`() {
    // Prepare a valid userId
    val userId = "user_123"
    val activity = "Push-ups"
    val duration = 10
    val reps = 15
    val sets = 3

    // Call the function
    addExerciseToWorkout(
        userId = userId,
        workoutViewModel = workoutViewModel,
        activity = activity,
        duration = duration,
        reps = reps,
        sets = sets)

    // Verify the function is called with correct parameters
    verify(workoutViewModel)
        .getOrAddExerciseToWorkout(
            uid = eq(userId),
            sessionId = argThat { startsWith("solo_") }, // Verifies sessionId starts with "solo_"
            exercise = eq(Exercise(activity, reps, sets, null, duration)),
            sessionType = eq(SessionType.SOLO))
  }
}
