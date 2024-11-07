package com.android.streetworkapp.model.progression

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProgressionRepositoryFirestore(private val db: FirebaseFirestore) : ProgressionRepository {

  companion object {
    private const val COLLECTION_PATH = "progressions"
  }

  override fun getNewProgressionId(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  override suspend fun getProgression(uid: String): Progression {
    return try {
      val document = db.collection(COLLECTION_PATH).whereEqualTo("location.id", uid).get().await()
      documentToProgression(document.documents.first())
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting progression. Reason: ${e.message}")
      Progression()
    }
  }

  override suspend fun updateProgressionWithAchievementAndGoal(
      progressionId: String,
      achievements: List<String>,
      goal: Int
  ) {
    try {
      db.collection(COLLECTION_PATH)
          .document(progressionId)
          .update("currentGoal", goal, "achievements", achievements)
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error: ${e.message}")
    }
  }

  override suspend fun createProgression(uid: String, progressionId: String) {
    require(uid.isNotEmpty())
    try {
      db.collection(COLLECTION_PATH)
          .document(progressionId)
          .set(Progression(progressionId, uid))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error: ${e.message}")
    }
  }

  private fun documentToProgression(document: DocumentSnapshot): Progression {
    return try {
      val progressionId = document.id
      val uid = document["uid"] as? String ?: ""
      val currentGoal = document["currentGoal"] as? Int ?: 0
      val eventsCreated = document["eventsCreated"] as? Int ?: 0
      val eventsJoined = document["eventsJoined"] as? Int ?: 0

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
