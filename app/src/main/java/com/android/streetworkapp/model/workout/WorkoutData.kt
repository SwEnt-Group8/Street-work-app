package com.android.streetworkapp.model.workout

/**
 * A data class representing a user's workout data.
 *
 * @param userUid The user's unique ID
 * @param workoutSessions List of workout sessions
 */
data class WorkoutData(
    val userUid: String = "",
    val workoutSessions: List<WorkoutSession> = emptyList()
)
