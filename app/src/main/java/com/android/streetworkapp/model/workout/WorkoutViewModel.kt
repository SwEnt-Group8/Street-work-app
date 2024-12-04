package com.android.streetworkapp.model.workout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

  private val _workoutData = MutableStateFlow<WorkoutData?>(null)
  val workoutData: StateFlow<WorkoutData?> = _workoutData.asStateFlow()

  /**
   * Fetches or initializes the user's WorkoutData.
   *
   * @param uid The UID of the user.
   */
  fun getOrAddWorkoutData(uid: String) {
    viewModelScope.launch { _workoutData.value = repository.getOrAddWorkoutData(uid) }
  }

  /**
   * Adds or updates a WorkoutSession in the user's WorkoutData.
   *
   * @param uid The UID of the user.
   * @param workoutSession The WorkoutSession to add or update.
   */
  fun addOrUpdateWorkoutSession(uid: String, workoutSession: WorkoutSession) {
    viewModelScope.launch {
      repository.addOrUpdateWorkoutSession(uid, workoutSession)
      refreshWorkoutData(uid)
    }
  }

  /**
   * Updates specific details of a WorkoutSession.
   *
   * @param uid The UID of the user.
   * @param sessionId The ID of the session to update.
   * @param exercises A list of exercises to add or update.
   * @param endTime The end time of the session.
   * @param winner The winner of the session.
   */
  fun updateWorkoutSessionDetails(
      uid: String,
      sessionId: String,
      exercises: List<Exercise> = emptyList(),
      endTime: Long? = null,
      winner: String? = null
  ) {
    viewModelScope.launch {
      repository.updateWorkoutSessionDetails(uid, sessionId, exercises, endTime, winner)
      refreshWorkoutData(uid)
    }
  }

  /**
   * Updates a specific exercise in a WorkoutSession.
   *
   * @param uid The UID of the user.
   * @param sessionId The ID of the session containing the exercise.
   * @param exerciseIndex The index of the exercise to update.
   * @param updatedExercise The updated exercise.
   */
  fun updateExercise(
      uid: String,
      sessionId: String,
      exerciseIndex: Int,
      updatedExercise: Exercise
  ) {
    viewModelScope.launch {
      repository.updateExercise(uid, sessionId, exerciseIndex, updatedExercise)
      refreshWorkoutData(uid)
    }
  }

  /**
   * Deletes a WorkoutSession from the user's WorkoutData.
   *
   * @param uid The UID of the user.
   * @param sessionId The ID of the session to delete.
   */
  fun deleteWorkoutSession(uid: String, sessionId: String) {
    viewModelScope.launch {
      repository.deleteWorkoutSession(uid, sessionId)
      refreshWorkoutData(uid)
    }
  }

  /**
   * Adds or updates a specific exercise in the user's workout data. If the exercise doesn't exist,
   * it adds it. If it exists, it updates the existing exercise.
   *
   * @param uid The UID of the user.
   * @param sessionId The ID of the workout session.
   * @param exercise The Exercise to add or update.
   */
  fun getOrAddExerciseToWorkout(
      uid: String,
      sessionId: String,
      exercise: Exercise,
      sessionType: SessionType
  ) {
    viewModelScope.launch {
      try {
        Log.d("WorkoutViewModel", "Fetching or adding workout data for UID: $uid")
        val currentWorkoutData = repository.getOrAddWorkoutData(uid)
        Log.d("WorkoutViewModel", "Current workout data: $currentWorkoutData")

        val updatedWorkoutSessions = currentWorkoutData.workoutSessions.toMutableList()

        // Find session or create a new one if it doesn't exist
        val sessionIndex = updatedWorkoutSessions.indexOfFirst { it.sessionId == sessionId }
        if (sessionIndex != -1) {
          // Update existing session
          val existingSession = updatedWorkoutSessions[sessionIndex]
          val updatedExercises = existingSession.exercises.toMutableList()
          val existingExercise = updatedExercises.find { it.name == exercise.name }
          if (existingExercise != null) {
            updatedExercises[updatedExercises.indexOf(existingExercise)] = exercise
          } else {
            updatedExercises.add(exercise)
          }
          updatedWorkoutSessions[sessionIndex] = existingSession.copy(exercises = updatedExercises)
        } else {
          // Create new session if none exists
          val newSession =
              WorkoutSession(
                  sessionId = sessionId,
                  sessionType = sessionType, // Pass the session type here
                  exercises = listOf(exercise),
                  startTime = System.currentTimeMillis() - (exercise.duration ?: 0),
                  endTime = System.currentTimeMillis())
          updatedWorkoutSessions.add(newSession)
        }

        Log.d("WorkoutViewModel", "Updated workout sessions: $updatedWorkoutSessions")

        // Update repository with the new/updated session
        repository.addOrUpdateWorkoutSession(uid, updatedWorkoutSessions.last())
        refreshWorkoutData(uid)
      } catch (e: Exception) {
        Log.e("WorkoutViewModel", "Error in getOrAddExerciseToWorkout: ${e.message}")
      }
    }
  }

  /**
   * Refreshes the current user's WorkoutData.
   *
   * @param uid The UID of the user.
   */
  private fun refreshWorkoutData(uid: String) {
    viewModelScope.launch { _workoutData.value = repository.getOrAddWorkoutData(uid) }
  }
}
