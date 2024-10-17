package com.android.streetworkapp.model.park

import android.util.Log
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** A repository interface using Firestore for park data. */
class ParkRepositoryFirestore(private val db: FirebaseFirestore) : ParkRepository {

  companion object {
    private const val COLLECTION_PATH = "parks"
  }

  val PID_EMPTY = "Park ID cannot be empty."

  /**
   * Get a new park ID.
   *
   * @return A new park ID.
   */
  override fun getNewPid(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  /**
   * Get a park by its ID.
   *
   * @param pid The park ID.
   * @return The park with the given ID, or null if the park does not exist.
   */
  override suspend fun getParkByPid(pid: String): Park? {
    require(pid.isNotEmpty()) { PID_EMPTY }
    return try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      documentToPark(document)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting park with ID: $pid. Reason: ${e.message}")
      null
    }
  }

  /**
   * Get a park by its location ID.
   *
   * @param locationId The location ID.
   * @return The park with the given location ID, or null if the park does not exist.
   */
  override suspend fun getParkByLocationId(locationId: String): Park? {
    require(locationId.isNotEmpty()) { "Location ID cannot be empty." }
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

  /**
   * Create a new park.
   *
   * @param park The park to create.
   */
  override suspend fun createPark(park: Park) {
    require(park.pid.isNotEmpty()) { PID_EMPTY }
    require(park.location.id.isNotEmpty()) { "Location ID cannot be empty." }
    try {
      db.collection(COLLECTION_PATH).document(park.pid).set(park).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error creating park: ${e.message}")
    }
  }

  /**
   * Update the name of a park.
   *
   * @param pid The park ID.
   * @param name The new name.
   */
  override suspend fun updateName(pid: String, name: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(name.isNotEmpty()) { "Name cannot be empty." }
    try {
      db.collection(COLLECTION_PATH).document(pid).update("name", name).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating park name: ${e.message}")
    }
  }

  /**
   * Update the image reference of a park.
   *
   * @param pid The park ID.
   * @param imageReference The new image reference.
   */
  override suspend fun updateImageReference(pid: String, imageReference: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(imageReference.isNotEmpty()) { "Image reference cannot be empty." }
    try {
      db.collection(COLLECTION_PATH).document(pid).update("imageReference", imageReference).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating park image reference: ${e.message}")
    }
  }

  /**
   * Add a rating to a park.
   *
   * @param pid The park ID.
   * @param rating The rating to add from 1 to 5.
   */
  override suspend fun addRating(pid: String, rating: Int) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(rating in 1..5) { "Rating must be between 1 and 5." }
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

  /**
   * Delete a rating from a park.
   *
   * @param pid The park ID.
   * @param rating The rating to delete from 1 to 5.
   */
  override suspend fun deleteRating(pid: String, rating: Int) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(rating in 1..5) { "Rating must be between 1 and 5." }
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

  /**
   * Update the capacity of a park.
   *
   * @param pid The park ID.
   * @param capacity The new capacity.
   */
  override suspend fun updateCapacity(pid: String, capacity: Int) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(capacity > 0) { "Capacity must be greater than 0." }
    try {
      db.collection(COLLECTION_PATH).document(pid).update("capacity", capacity).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating capacity from park: ${e.message}")
    }
  }

  /**
   * Increment the occupancy of a park by one person.
   *
   * @param pid The park ID.
   */
  override suspend fun incrementOccupancy(pid: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentOccupancy = document.getLong("occupancy")?.toInt() ?: 0

      db.collection(COLLECTION_PATH).document(pid).update("occupancy", currentOccupancy + 1).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error incrementing occupancy for park: ${e.message}")
    }
  }

  /**
   * Decrement the occupancy of a park by one person.
   *
   * @param pid The park ID.
   */
  override suspend fun decrementOccupancy(pid: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    try {
      val document = db.collection(COLLECTION_PATH).document(pid).get().await()
      val currentOccupancy = document.getLong("occupancy")?.toInt() ?: 0

      val newOccupancy = if (currentOccupancy > 0) currentOccupancy - 1 else 0

      db.collection(COLLECTION_PATH).document(pid).update("occupancy", newOccupancy).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error decrementing occupancy for park: ${e.message}")
    }
  }

  /**
   * Add an event to a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  override suspend fun addEventToPark(pid: String, eid: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(eid.isNotEmpty()) { "Event ID cannot be empty." }
    try {
      db.collection(COLLECTION_PATH)
          .document(pid)
          .update("events", FieldValue.arrayUnion(eid))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding event to park: ${e.message}")
    }
  }

  /**
   * Delete an event from a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  override suspend fun deleteEventFromPark(pid: String, eid: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    require(eid.isNotEmpty()) { "Event ID cannot be empty." }
    try {
      db.collection(COLLECTION_PATH)
          .document(pid)
          .update("events", FieldValue.arrayRemove(eid))
          .await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting event from park: ${e.message}")
    }
  }

  /**
   * Delete a park by its ID.
   *
   * @param pid The park ID.
   */
  override suspend fun deleteParkByPid(pid: String) {
    require(pid.isNotEmpty()) { PID_EMPTY }
    try {
      db.collection(COLLECTION_PATH).document(pid).delete().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting park: ${e.message}")
    }
  }

  /**
   * Convert a Firestore document to a park object.
   *
   * @param document The Firestore document.
   * @return The park object, or null if the conversion failed.
   */
  private fun documentToPark(document: DocumentSnapshot): Park? {
    return try {
      val pid = document.id
      val name = document["name"] as? String ?: ""
      val location = document["location"] as? ParkLocation ?: ParkLocation(0.0, 0.0, "0")
      val imageReference = document["imageReference"] as? String ?: ""
      val rating = (document["rating"] as? Double)?.toFloat() ?: 0.0f
      val nbrRating = (document["nbrRating"] as? Long)?.toInt() ?: 0
      val capacity = (document["capacity"] as? Long)?.toInt() ?: 0
      val occupancy = (document["occupancy"] as? Long)?.toInt() ?: 0

      val events =
          try {
            (document["events"] as? List<*>)?.filterIsInstance<String>() ?: emptyList<String>()
          } catch (e: Exception) {
            Log.e("FirestoreError", "Error retrieving events list", e)
            emptyList<String>()
          }

      Park(pid, name, location, imageReference, rating, nbrRating, capacity, occupancy, events)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error converting document to park: ${e.message}")
      null
    }
  }
}
