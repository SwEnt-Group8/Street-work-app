package com.android.streetworkapp.model.workout

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
   */
  fun updateWorkoutSessionDetails(
      uid: String,
      sessionId: String,
      exercises: List<Exercise>,
      endTime: Long?
  ) {
    viewModelScope.launch {
      repository.updateWorkoutSessionDetails(uid, sessionId, exercises, endTime)
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
   * Refreshes the current user's WorkoutData.
   *
   * @param uid The UID of the user.
   */
  private fun refreshWorkoutData(uid: String) {
    viewModelScope.launch { _workoutData.value = repository.getOrAddWorkoutData(uid) }
  }
}
