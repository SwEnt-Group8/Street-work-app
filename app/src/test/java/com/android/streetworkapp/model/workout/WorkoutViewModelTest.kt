package com.android.streetworkapp.model.workout

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

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

  @Test
  fun `getOrAddExerciseToWorkout adds a new session if none exists`() = runTest {
    // Arrange
    val uid = "user123"
    val sessionId = "solo_session_1"
    val exercise = Exercise("Push-ups", 10, 3, 30f, 0)
    val sessionType = SessionType.SOLO

    // Mock the repository to return empty workout data
    whenever(repository.getOrAddWorkoutData(uid))
        .thenReturn(WorkoutData(userUid = uid, workoutSessions = emptyList()))

    // Act
    workoutViewModel.getOrAddExerciseToWorkout(uid, sessionId, exercise, sessionType)
    testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

    // Assert
    val uidCaptor = argumentCaptor<String>()
    val sessionCaptor = argumentCaptor<WorkoutSession>()

    // Verify the repository method was called
    verify(repository).addOrUpdateWorkoutSession(uidCaptor.capture(), sessionCaptor.capture())

    // Debug captor values
    println("Captured UID: ${uidCaptor.allValues}")
    println("Captured Sessions: ${sessionCaptor.allValues}")

    // Verify that the UID is correct
    assertEquals(uid, uidCaptor.firstValue)

    // Verify the captured session
    val capturedSession = sessionCaptor.firstValue
    assertEquals(sessionId, capturedSession.sessionId)
    assertEquals(sessionType, capturedSession.sessionType)
    assertEquals(1, capturedSession.exercises.size)
    assertEquals(exercise, capturedSession.exercises.first())
  }
  @Test
  fun sendPairingRequestCallsRepository() = runTest {
    val fromUid = "fromUser"
    val toUid = "toUser"
    workoutViewModel.sendPairingRequest(fromUid, toUid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).sendPairingRequest(fromUid, toUid)
  }

  @Test
  fun observePairingRequestsUpdatesFlowCorrectly() = runTest {
    val uid = "testUid"
    val pairingRequest = PairingRequest(requestId = "reqId", fromUid = "fromUser", toUid = "toUser")
    val flow = flowOf(listOf(pairingRequest))
    `when`(repository.observePairingRequests(uid)).thenReturn(flow)

    workoutViewModel.observePairingRequests(uid)
    testDispatcher.scheduler.advanceUntilIdle()

    assertEquals(listOf(pairingRequest), workoutViewModel.pairingRequests.value)
  }

  @Test
  fun respondToPairingRequestCallsRepository() = runTest {
    val requestId = "requestId"
    val isAccepted = true
    workoutViewModel.respondToPairingRequest(requestId, isAccepted)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).respondToPairingRequest(requestId, isAccepted)
  }

  @Test
  fun observeWorkoutSessionsUpdatesFlowCorrectly() = runTest {
    val uid = "testUid"
    val session = WorkoutSession(sessionId = "sessionId", startTime = 0L, endTime = 0L, sessionType = SessionType.SOLO)
    val flow = flowOf(listOf(session))
    `when`(repository.observeWorkoutSessions(uid)).thenReturn(flow)

    workoutViewModel.observeWorkoutSessions(uid)
    testDispatcher.scheduler.advanceUntilIdle()

    assertEquals(listOf(session), workoutViewModel.workoutSessions.value)
  }

  @Test
  fun addCommentToSessionCallsRepository() = runTest {
    val sessionId = "sessionId"
    val comment = Comment(authorUid = "user123", text = "Nice workout!")

    workoutViewModel.addCommentToSession(sessionId, comment)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addCommentToSession(sessionId, comment)
  }
}
