package com.android.streetworkapp.model.event

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
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

  override suspend fun getEventByEid(eid: String): Event? {
    require(eid.isNotEmpty())
    return try {
      val document = db.collection(COLLECTION_PATH).document(eid).get().await()
      documentToEvent(document)
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error getting event with ID: $eid. Reason: ${e.message}")
      null
    }
  }

  /**
   * Fetch all events from the database.
   *
   * @param onSuccess The callback to execute on success.
   * @param onFailure The callback to execute on failure.
   */
  override suspend fun getEvents(onSuccess: (List<Event>) -> Unit, onFailure: (Exception) -> Unit) {
    try {
      db.collection(COLLECTION_PATH)
          .get()
          .addOnSuccessListener { documents ->
            val eventList = documents.documents.mapNotNull { documentToEvent(it) }
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

  private fun documentToEvent(document: DocumentSnapshot): Event? {
    return if (document.exists()) {
      Event(
          eid = document.id,
          title = document.get("title") as String,
          description = document.get("description") as String,
          participants = (document.get("participants") as Long).toInt(),
          maxParticipants = (document.get("maxParticipants") as Long).toInt(),
          date = document.get("date") as Timestamp,
          owner = document.get("owner") as String,
          listParticipants = document.get("listParticipants") as List<String>,
          parkId = document.get("parkId") as String)
    } else {
      null
    }
  }
}
