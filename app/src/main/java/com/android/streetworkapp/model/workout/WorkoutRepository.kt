package com.android.streetworkapp.model.workout

interface WorkoutRepository {
  suspend fun getOrAddWorkoutData(uid: String): WorkoutData

  suspend fun addOrUpdateWorkoutSession(uid: String, workoutSession: WorkoutSession)

  suspend fun updateWorkoutSessionDetails(
      uid: String,
      sessionId: String,
      exercises: List<Exercise>,
      endTime: Long?
  )

  suspend fun deleteWorkoutSession(uid: String, sessionId: String)
}
