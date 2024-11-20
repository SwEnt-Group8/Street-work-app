package com.android.streetworkapp.model.workout

/**
 * A data class representing a workout session.
 *
 * @param sessionId Unique ID for the session
 * @param startTime Epoch timestamp for the start of the session
 * @param endTime Epoch timestamp for the end of the session
 * @param sessionType Type of session (e.g., ALONE, REFEREE, CHALLENGE)
 * @param participants List of participants in the session
 * @param exercises List of exercises performed in the session
 * @param winner The winner of the session (only applicable for CHALLENGE sessions)
 */
data class WorkoutSession(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long,
    val sessionType: SessionType,
    val participants: List<String> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val winner: String? = null
)

/** An enum class representing the type of workout session. */
enum class SessionType {
  ALONE, // Solo session
  REFEREE, // Referee-observed session
  CHALLENGE // Competitive session
}
