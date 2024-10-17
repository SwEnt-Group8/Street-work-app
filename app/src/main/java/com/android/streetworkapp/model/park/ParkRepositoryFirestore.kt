package com.android.streetworkapp.model.park

import android.util.Log
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ParkRepositoryFirestore(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ParkRepository {

  companion object {
    private const val COLLECTION_PATH = "parks"
  }

  override fun getNewPid(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  override suspend fun getParkByPid(pid: String): Park? {
    require(pid.isNotEmpty()) {}
    return try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      documentToPark(document)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting park with ID: $pid. Reason: ${e.message}")
      null
    }
  }

  override suspend fun getParkByLocationId(locationId: String): Park? {
    require(locationId.isNotEmpty()) {}
    return try {
      val document =
          db.collection(COLLECTION_PATH).whereEqualTo("location.id", locationId).get().await()
      documentToPark(document.documents.first())
    } catch (e: Exception) {
      Log.e(
          "FirestoreError",
          "Error getting park with location ID: $locationId. Reason: ${e.message}")
      null
    }
  }

  override suspend fun createPark(park: Park) {
    require(park.pid.isNotEmpty()) {}
    require(park.location.id.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH).document(park.pid).set(park).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error creating park: ${e.message}")
    }
  }

  override suspend fun updateName(pid: String, name: String) {
    require(pid.isNotEmpty()) {}
    require(name.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH).document(pid).update("name", name).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating park name: ${e.message}")
    }
  }

  override suspend fun updateImageReference(pid: String, imageReference: String) {
    require(pid.isNotEmpty()) {}
    require(imageReference.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH).document(pid).update("imageReference", imageReference).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating park image reference: ${e.message}")
    }
  }

  override suspend fun addRating(pid: String, rating: Int) {
    require(pid.isNotEmpty()) {}
    require(rating in 1..5) {}
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentRating = document.getDouble("rating") ?: 0.0
      val currentNbrRating = document.getLong("nbrRating")?.toInt() ?: 0

      val newNbrRating = currentNbrRating + 1
      val newRating = ((currentRating * currentNbrRating) + rating) / newNbrRating

      db.collection(COLLECTION_PATH)
          .document(pid)
          .update(mapOf("rating" to newRating, "nbrRating" to newNbrRating))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding rating to park: ${e.message}")
    }
  }

  override suspend fun deleteRating(pid: String, rating: Int) {
    require(pid.isNotEmpty()) {}
    require(rating in 1..5) {}
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentRating = document.getDouble("rating") ?: 0.0
      val currentNbrRating = document.getLong("nbrRating")?.toInt() ?: 0

      if (currentNbrRating > 0) {
        val newNbrRating = currentNbrRating - 1
        val newRating =
            if (newNbrRating == 0) 0.0
            else ((currentRating * currentNbrRating) - rating) / newNbrRating

        db.collection(COLLECTION_PATH)
            .document(pid)
            .update(mapOf("rating" to newRating, "nbrRating" to newNbrRating))
            .await()
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting rating from park: ${e.message}")
    }
  }

  override suspend fun updateCapacity(pid: String, capacity: Int) {
    require(pid.isNotEmpty()) {}
    require(capacity > 0) {}
    try {
      db.collection(COLLECTION_PATH).document(pid).update("capacity", capacity).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating capacity from park: ${e.message}")
    }
  }

  override suspend fun incrementOccupancy(pid: String) {
    require(pid.isNotEmpty()) {}
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentOccupancy = document.getLong("occupancy")?.toInt() ?: 0

      db.collection(COLLECTION_PATH).document(pid).update("occupancy", currentOccupancy + 1).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error incrementing occupancy for park: ${e.message}")
    }
  }

  override suspend fun decrementOccupancy(pid: String) {
    require(pid.isNotEmpty()) {}
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentOccupancy = document.getLong("occupancy")?.toInt() ?: 0

      val newOccupancy = if (currentOccupancy > 0) currentOccupancy - 1 else 0

      db.collection(COLLECTION_PATH).document(pid).update("occupancy", newOccupancy).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error decrementing occupancy for park: ${e.message}")
    }
  }

  override suspend fun addEventToPark(pid: String, eid: String) {
    require(pid.isNotEmpty()) {}
    require(eid.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH)
          .document(pid)
          .update("events", FieldValue.arrayUnion(eid))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding event to park: ${e.message}")
    }
  }

  override suspend fun deleteEventFromPark(pid: String, eid: String) {
    require(pid.isNotEmpty()) {}
    require(eid.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH)
          .document(pid)
          .update("events", FieldValue.arrayRemove(eid))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting event from park: ${e.message}")
    }
  }

  override suspend fun deleteParkByPid(pid: String) {
    require(pid.isNotEmpty()) {}
    try {
      db.collection(COLLECTION_PATH).document(pid).delete().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting park: ${e.message}")
    }
  }

  private fun documentToPark(document: DocumentSnapshot): Park? {
    return try {
      val pid = document.id
      val name = document.getString("name") ?: ""
      val location = document.get("location") as? ParkLocation ?: ParkLocation(0.0, 0.0, "0")
      val rating = document.getDouble("rating")?.toFloat() ?: 0.0f
      val nbrRating = document.getLong("nbrRating")?.toInt() ?: 0
      val capacity = document.getLong("capacity")?.toInt() ?: 0
      val occupancy = document.getLong("occupancy")?.toInt() ?: 0

      val events =
          try {
            document["events"] as? List<*> ?: emptyList<String>()
          } catch (e: Exception) {
            Log.e("FirestoreError", "Error retrieving events list", e)
            emptyList<String>()
          }

      Park(pid, name, location, "", rating, nbrRating, capacity, occupancy, emptyList<String>())
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error converting document to park: ${e.message}")
      null
    }
  }
}
