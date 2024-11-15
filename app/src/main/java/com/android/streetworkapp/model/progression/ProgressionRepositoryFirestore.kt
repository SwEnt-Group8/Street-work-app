package com.android.streetworkapp.model.progression

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProgressionRepositoryFirestore(private val db: FirebaseFirestore) : ProgressionRepository {

  companion object {
    private const val COLLECTION_PATH = "progressions"
    private const val ERROR_UID_EMPTY = "The uid must not be empty."
    private const val ERROR_PID_EMPTY = "The progression id must not be empty."
  }

  /** Used to have a unique progressionId in the database. */
  override fun getNewProgressionId(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  /**
   * Returns the existing progression linked to the uid, or creates a new one if none is found
   *
   * @param uid: The uid (User Id)
   */

  // Note: for reviewers, the viewmodel implementation that was done wasn't compatible with our
  // userviewmodel, this is a quick fix I made, will need to rework the whole viewmodel in later pr
  override suspend fun getOrAddProgression(uid: String): Progression {
    require(uid.isNotEmpty()) { "Empty UID" }
    return try {
      val document = db.collection(COLLECTION_PATH).whereEqualTo("uid", uid).get().await()
      if (document.isEmpty) {
        val progressionId = this.getNewProgressionId()
        val progression = Progression(progressionId, uid, Ranks.BRONZE.score)
        db.collection(COLLECTION_PATH).document().set(progression).await()

        progression
      } else {
        documentToProgression(document.documents[0])
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting or creating Progression from user uid: ${e.message}")
      Progression("", "", 0, 0, 0, emptyList())
    }
  }

  /**
   * Fetch the progression linked to the given uid
   *
   * @param uid: The uid (User Id)
   * @param onSuccess The callback to execute on success.
   * @param onFailure The callback to execute on failure.
   */
  override fun getProgression(
      uid: String,
      onSuccess: (Progression) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    try {
      db.collection(COLLECTION_PATH)
          .whereEqualTo("uid", uid)
          .get()
          .addOnSuccessListener { documents ->
            val progression = documentToProgression(documents.documents[0])
            onSuccess(progression)
          }
          .addOnFailureListener(onFailure)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting progression. Reason: ${e.message}")
    }
  }

  /**
   * Used to change the next goal and to add new achievements.
   *
   * @param progressionId: the id of a progression object
   * @param achievements The new list of achievements
   * @param goal The current goal (a score)
   */
  override suspend fun updateProgressionWithAchievementAndGoal(
      progressionId: String,
      achievements: List<String>,
      goal: Int
  ) {
    require(progressionId.isNotEmpty()) { ERROR_PID_EMPTY }
    try {
      db.collection(COLLECTION_PATH)
          .document(progressionId)
          .update("currentGoal", goal, "achievements", achievements)
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error: ${e.message}")
    }
  }

  /**
   * Used to create a new progression object in the database
   *
   * @param progressionId: the id of a progression object
   * @param uid: The new list of achievements
   */
  override suspend fun createProgression(uid: String, progressionId: String) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }
    try {
      db.collection(COLLECTION_PATH)
          .document(progressionId)
          .set(Progression(progressionId, uid, Ranks.BRONZE.score))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error: ${e.message}")
    }
  }

  /**
   * Used to convert a document to a Progression Object
   *
   * @param document: the document to convert
   */
  fun documentToProgression(document: DocumentSnapshot): Progression {
    return try {
      val progressionId = document.id
      val uid = document["uid"] as? String ?: ""
      val currentGoal = (document["currentGoal"] as? Long)?.toInt() ?: 0
      val eventsCreated = (document["eventsCreated"] as? Long)?.toInt() ?: 0
      val eventsJoined = (document["eventsJoined"] as? Long)?.toInt() ?: 0
      val achievements =
          try {
            (document["achievements"] as? List<*>)?.filterIsInstance<String>()
                ?: emptyList<String>()
          } catch (e: Exception) {
            Log.e("FirestoreError", "Error retrieving achievements list", e)
            emptyList<String>()
          }
      Progression(progressionId, uid, currentGoal, eventsCreated, eventsJoined, achievements)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error converting document: ${e.message}")
      Progression()
    }
  }
}
