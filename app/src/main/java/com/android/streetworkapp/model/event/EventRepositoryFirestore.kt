package com.android.streetworkapp.model.event

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** A repository interface using Firestore for park data. */
class EventRepositoryFirestore(private val db: FirebaseFirestore) : EventRepository {

  companion object {
    private const val COLLECTION_PATH = "events"
  }

  /**
   * Get a new event ID.
   *
   * @return A new event ID.
   */
  override fun getNewEid(): String {
    return db.collection(COLLECTION_PATH).document().id
  }

  /**
   * Fetch all events from the database.
   *
   * @param onSuccess The callback to execute on success.
   * @param onFailure The callback to execute on failure.
   */
  override fun getEvents(onSuccess: (List<Event>) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      db.collection(COLLECTION_PATH)
          .get()
          .addOnSuccessListener { documents ->
            val eventList =
                documents.documents.mapNotNull {
                  Event(
                      eid = it.id,
                      title = it.get("title") as String,
                      description = it.get("description") as String,
                      participants = (it.get("participants") as Long).toInt(),
                      maxParticipants = (it.get("maxParticipants") as Long).toInt(),
                      date = it.get("date") as Timestamp,
                      owner = it.get("owner") as String,
                      listParticipants = it.get("listParticipants") as List<String>,
                      parkId = it.get("parkId") as String)
                }
            onSuccess(eventList)
          }
          .addOnFailureListener(onFailure)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting events: ${e.message}")
    }
  }

  /**
   * Add a new event in the database.
   *
   * @param event The event to add.
   */
  override suspend fun addEvent(event: Event) {
    require(event.eid.isNotEmpty())
    try {
      db.collection(COLLECTION_PATH).document(event.eid).set(event).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error creating event: ${e.message}")
    }
  }
}
