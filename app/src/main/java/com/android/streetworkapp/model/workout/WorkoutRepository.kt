package com.android.streetworkapp.model.workout

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
}
