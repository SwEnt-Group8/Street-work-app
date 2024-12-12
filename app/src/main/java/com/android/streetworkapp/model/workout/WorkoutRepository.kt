package com.android.streetworkapp.model.workout

import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
  suspend fun getOrAddWorkoutData(uid: String): WorkoutData

  suspend fun addOrUpdateWorkoutSession(uid: String, workoutSession: WorkoutSession)

  suspend fun updateWorkoutSessionDetails(
      uid: String,
      sessionId: String,
      exercises: List<Exercise> = emptyList(),
      endTime: Long? = null,
      winner: String? = null
  )

  suspend fun updateExercise(
      uid: String,
      sessionId: String,
      exerciseIndex: Int,
      updatedExercise: Exercise
  )

  suspend fun deleteWorkoutSession(uid: String, sessionId: String)

  suspend fun saveWorkoutData(uid: String, workoutData: WorkoutData)

  suspend fun sendPairingRequest(fromUid: String, toUid: String)

  fun observePairingRequests(uid: String): Flow<List<PairingRequest>>

  suspend fun respondToPairingRequest(requestId: String, isAccepted: Boolean)

  fun observeWorkoutSessions(uid: String): Flow<List<WorkoutSession>>

  suspend fun addCommentToSession(sessionId: String, comment: Comment)

  suspend fun deleteWorkoutDataByUid(uid: String)
}
