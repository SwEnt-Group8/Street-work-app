package com.android.streetworkapp.model.workout

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class WorkoutViewModelTest {

  private lateinit var repository: WorkoutRepository
  private lateinit var workoutViewModel: WorkoutViewModel
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock(WorkoutRepository::class.java)
    workoutViewModel = WorkoutViewModel(repository)
  }

  @Test
  fun getOrAddWorkoutDataCallsRepository() = runTest {
    val uid = "testUid"
    workoutViewModel.getOrAddWorkoutData(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getOrAddWorkoutData(uid)
  }

  @Test
  fun addOrUpdateWorkoutSessionCallsRepository() = runTest {
    val uid = "testUid"
    val workoutSession =
        WorkoutSession(
            sessionId = "testSessionId",
            startTime = 0L,
            endTime = 0L,
            sessionType = SessionType.SOLO)

    workoutViewModel.addOrUpdateWorkoutSession(uid, workoutSession)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addOrUpdateWorkoutSession(uid, workoutSession)
  }

  @Test
  fun updateWorkoutSessionDetailsCallsRepository() = runTest {
    val uid = "testUid"
    val sessionId = "testSessionId"
    val exercises = listOf(Exercise(name = "Push-up"))
    val endTime = 123456789L

    workoutViewModel.updateWorkoutSessionDetails(uid, sessionId, exercises, endTime)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateWorkoutSessionDetails(uid, sessionId, exercises, endTime)
  }

  @Test
  fun updateExerciseCallsRepository() = runTest {
    val uid = "testUid"
    val sessionId = "testSessionId"
    val exerciseIndex = 0
    val updatedExercise = Exercise(name = "Sit-up")

    workoutViewModel.updateExercise(uid, sessionId, exerciseIndex, updatedExercise)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateExercise(uid, sessionId, exerciseIndex, updatedExercise)
  }

  @Test
  fun deleteWorkoutSessionCallsRepository() = runTest {
    val uid = "testUid"
    val sessionId = "testSessionId"

    workoutViewModel.deleteWorkoutSession(uid, sessionId)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteWorkoutSession(uid, sessionId)
  }

  @Test
  fun workoutDataStateFlowUpdatesCorrectly() = runTest {
    val uid = "testUid"
    val workoutData = WorkoutData(uid, listOf())

    `when`(repository.getOrAddWorkoutData(uid)).thenReturn(workoutData)
    workoutViewModel.getOrAddWorkoutData(uid)
    testDispatcher.scheduler.advanceUntilIdle()

    assertEquals(workoutData, workoutViewModel.workoutData.value)
  }

  @Test
  fun getOrAddExerciseToWorkoutHandlesSessionCorrectly() = runTest {
    val uid = "testUid"
    val sessionId = "testSessionId"
    val exercise = Exercise(name = "Push-up", duration = 30)
    val sessionType = SessionType.SOLO

    val existingSession =
        WorkoutSession(
            sessionId = sessionId,
            startTime = 0L,
            endTime = 0L,
            sessionType = sessionType,
            participants = listOf("testParticipant"),
            exercises = listOf(),
            winner = null)
    val workoutData = WorkoutData(userUid = uid, workoutSessions = listOf(existingSession))

    `when`(repository.getOrAddWorkoutData(uid)).thenReturn(workoutData)

    workoutViewModel.getOrAddExerciseToWorkout(uid, sessionId, exercise, sessionType)
    testDispatcher.scheduler.advanceUntilIdle()

    verify(repository, times(2)).getOrAddWorkoutData(uid)
  }
}
