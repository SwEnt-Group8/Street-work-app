package com.android.streetworkapp.model.workout

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkoutRepositoryFirestore(private val db: FirebaseFirestore) : WorkoutRepository {

  companion object {
    private const val COLLECTION_PATH = "workoutData"
    private const val ERROR_UID_EMPTY = "The user UID must not be empty."
    private const val ERROR_TAG = "FirestoreError"
    private const val ERROR_SESSION_ID_EMPTY = "The session ID must not be empty."
    private const val WORKOUT_SESSIONS = "workoutSessions"
  }

  /**
   * Retrieves the WorkoutData for a user or initializes a new one if none exists.
   *
   * @param uid The UID of the user.
   */
  override suspend fun getOrAddWorkoutData(uid: String): WorkoutData {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    return try {
      val document = db.collection(COLLECTION_PATH).document(uid).get().await()
      if (!document.exists()) {
        val workoutData = WorkoutData(uid, emptyList())
        db.collection(COLLECTION_PATH).document(uid).set(workoutData).await()
        workoutData
      } else {
        documentToWorkoutData(document)
      }
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error getting or creating WorkoutData for user UID: ${e.message}")
      WorkoutData(uid, emptyList())
    }
  }

  /**
   * Adds or updates a WorkoutSession in the user's WorkoutData.
   *
   * @param uid The UID of the user (also the WorkoutData ID).
   * @param workoutSession The WorkoutSession to add or update.
   */
  override suspend fun addOrUpdateWorkoutSession(uid: String, workoutSession: WorkoutSession) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    require(workoutSession.sessionId.isNotEmpty()) { ERROR_SESSION_ID_EMPTY }
    try {
      val workoutData = getOrAddWorkoutData(uid)
      val updatedSessions =
          workoutData.workoutSessions.toMutableList().apply {
            removeAll { it.sessionId == workoutSession.sessionId }
            add(workoutSession)
          }
      db.collection(COLLECTION_PATH).document(uid).update(WORKOUT_SESSIONS, updatedSessions).await()
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error adding or updating WorkoutSession: ${e.message}")
    }
  }

  /**
   * Updates specific details of a WorkoutSession.
   *
   * @param uid The UID of the user (also the WorkoutData ID).
   * @param sessionId The ID of the session to update.
   * @param exercises A list of exercises to add or update, if empty the exercises are not updated.
   * @param endTime The end time of the session, if null the end time is not updated.
   * @param winner The winner of the session, if null the winner is not updated.
   */
  override suspend fun updateWorkoutSessionDetails(
      uid: String,
      sessionId: String,
      exercises: List<Exercise>,
      endTime: Long?,
      winner: String?
  ) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    require(sessionId.isNotEmpty()) { ERROR_SESSION_ID_EMPTY }

    try {
      // Construct the field path for the specific session details
      val updateMap = mutableMapOf<String, Any?>()

      if (exercises.isNotEmpty()) {
        updateMap["$WORKOUT_SESSIONS.$sessionId.exercises"] = exercises
      }
      if (endTime != null) {
        updateMap["$WORKOUT_SESSIONS.$sessionId.endTime"] = endTime
      }
      if (!winner.isNullOrEmpty()) {
        updateMap["$WORKOUT_SESSIONS.$sessionId.winner"] = winner
      }
      // Perform targeted update in Firestore
      if (updateMap.isNotEmpty()) {
        db.collection(COLLECTION_PATH).document(uid).update(updateMap).await()
      }
    } catch (e: Exception) {
      Log.e(
          ERROR_TAG, "Error updating WorkoutSession details for sessionId=$sessionId: ${e.message}")
    }
  }

  /**
   * Updates a specific exercise in a WorkoutSession.
   *
   * @param uid The UID of the user (also the WorkoutData ID).
   * @param sessionId The ID of the session containing the exercise.
   * @param exerciseIndex The index of the exercise to update.
   * @param updatedExercise The updated exercise.
   */
  override suspend fun updateExercise(
      uid: String,
      sessionId: String,
      exerciseIndex: Int,
      updatedExercise: Exercise
  ) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    require(sessionId.isNotEmpty()) { ERROR_SESSION_ID_EMPTY }

    try {
      val fieldPath = "$WORKOUT_SESSIONS.$sessionId.exercises.$exerciseIndex"
      db.collection(COLLECTION_PATH)
          .document(uid)
          .update(mapOf(fieldPath to updatedExercise))
          .await()
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error updating Exercise: ${e.message}")
    }
  }

  /**
   * Deletes a specific WorkoutSession from the user's WorkoutData.
   *
   * @param uid The UID of the user (also the WorkoutData ID).
   * @param sessionId The ID of the session to delete.
   */
  override suspend fun deleteWorkoutSession(uid: String, sessionId: String) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    require(sessionId.isNotEmpty()) { ERROR_SESSION_ID_EMPTY }
    try {
      val workoutData = getOrAddWorkoutData(uid)
      val updatedSessions = workoutData.workoutSessions.filter { it.sessionId != sessionId }
      db.collection(COLLECTION_PATH).document(uid).update(WORKOUT_SESSIONS, updatedSessions).await()
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error deleting WorkoutSession: ${e.message}")
    }
  }

  /**
   * Saves or overwrites the entire WorkoutData for a user in Firestore.
   *
   * @param uid The UID of the user.
   * @param workoutData The WorkoutData to save.
   */
  override suspend fun saveWorkoutData(uid: String, workoutData: WorkoutData) {
    require(uid.isNotEmpty()) { "The user UID must not be empty." }
    try {
      db.collection(COLLECTION_PATH).document(uid).set(workoutData).await()
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error saving WorkoutData for UID=$uid: ${e.message}")
    }
  }

  /**
   * Deletes the entire WorkoutData for a user from Firestore.
   *
   * @param uid The UID of the user.
   */
  override suspend fun deleteWorkoutDataByUid(uid: String) {
    require(uid.isNotEmpty()) { "The user UID must not be empty." }
    try {
      db.collection(COLLECTION_PATH).document(uid).delete().await()
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error deleting WorkoutData for UID=$uid: ${e.message}")
    }
  }

  /**
   * Converts a Firestore document to a WorkoutData object.
   *
   * @param document The document to convert.
   */
  private fun documentToWorkoutData(document: DocumentSnapshot): WorkoutData {
    return try {
      val userUid = document.id
      val workoutSessions =
          (document[WORKOUT_SESSIONS] as? List<*>)
              ?.filterIsInstance<Map<String, Any>>()
              ?.map(::parseWorkoutSession) ?: emptyList()
      WorkoutData(userUid, workoutSessions)
    } catch (e: Exception) {
      Log.e(ERROR_TAG, "Error converting document to WorkoutData: ${e.message}")
      WorkoutData("Error check logcat", emptyList())
    }
  }

  /**
   * Converts a session map to a WorkoutSession object.
   *
   * @param sessionMap The map representing the session.
   */
  private fun parseWorkoutSession(sessionMap: Map<String, Any>): WorkoutSession {
    return WorkoutSession(
        sessionId = sessionMap["sessionId"] as? String ?: "",
        startTime = (sessionMap["startTime"] as? Long) ?: 0L,
        endTime = (sessionMap["endTime"] as? Long) ?: 0L,
        sessionType = SessionType.valueOf(sessionMap["sessionType"] as? String ?: "ALONE"),
        participants =
            (sessionMap["participants"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        exercises =
            parseExercises(
                (sessionMap["exercises"] as? List<*>)?.filterIsInstance<Map<String, Any>>()
                    ?: emptyList()),
        winner = sessionMap["winner"] as? String)
  }

  /**
   * Converts a list of exercise maps to a list of Exercise objects.
   *
   * @param exerciseMaps The list of exercise maps to convert.
   */
  private fun parseExercises(exerciseMaps: List<Map<String, Any>>): List<Exercise> {
    return exerciseMaps.map { exerciseMap ->
      Exercise(
          name = exerciseMap["name"] as? String ?: "",
          reps = (exerciseMap["reps"] as? Long)?.toInt(),
          sets = (exerciseMap["sets"] as? Long)?.toInt(),
          weight = (exerciseMap["weight"] as? Double)?.toFloat(),
          duration = (exerciseMap["duration"] as? Long)?.toInt())
    }
  }
}
