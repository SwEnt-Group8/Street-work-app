package com.android.streetworkapp.model.workout

import com.android.streetworkapp.utils.toFormattedString

/**
 * A data class representing a workout session.
 *
 * @param sessionId Unique ID for the session
 * @param startTime Epoch timestamp for the start of the session
 * @param endTime Epoch timestamp for the end of the session
 * @param sessionType Type of session (e.g., SOLO, COACH, CHALLENGE)
 * @param participants List of participants in the session
 * @param exercises List of exercises performed in the session
 * @param winner The winner of the session (only applicable for CHALLENGE sessions)
 * @param status The status of the session (e.g., PENDING, ONGOING, COMPLETED)
 * @param coachUid The UID of the coach (only applicable for COACH sessions)
 * @param comments List of comments on the session
 */
data class WorkoutSession(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long,
    val sessionType: SessionType,
    val participants: List<String> = emptyList(),
    val exercises: List<Exercise> = emptyList(),
    val winner: String? = null,
    val status: SessionStatus = SessionStatus.ONGOING,
    val coachUid: String? = null,
    val comments: List<Comment> = emptyList()
) {
  fun getFormattedStartTime(): String = startTime.toFormattedString()

  fun getFormattedEndTime(): String = endTime.toFormattedString()
}

/** An enum class representing the type of workout session. */
enum class SessionType {
  SOLO, // Solo session
  COACH, // Referee-observed session
  CHALLENGE // Competitive session
}

/** An enum class representing the status of a workout session. */
enum class SessionStatus {
  PENDING, // Session waiting for confirmation
  ONGOING, // Session in progress
  COMPLETED // Session finished
}

/**
 * A data class representing an exercise performed in a workout session.
 *
 * @param authorUid UID of the author of the comment
 * @param text The text content of the comment
 * @param timestamp The timestamp of the comment creation
 */
data class Comment(
    val authorUid: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
