package com.android.streetworkapp.model.event

import android.util.Log
import com.android.streetworkapp.model.park.Park
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
  override suspend fun getEvents(
      park: Park,
      onSuccess: (List<Event>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val eventList = park.events.mapNotNull { getEventByEid(it) }
      onSuccess(eventList)
    } catch (e: Exception) {
      onFailure(e)
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

  fun documentToEvent(document: DocumentSnapshot): Event? {
    return if (document.exists()) {
      Event(
          eid = document.id,
          title = document["title"] as String,
          description = document["description"] as String,
          participants = (document["participants"] as Long).toInt(),
          maxParticipants = (document["maxParticipants"] as Long).toInt(),
          date = document["date"] as Timestamp,
          owner = document["owner"] as String,
          listParticipants = (document["listParticipants"] as List<*>).filterIsInstance<String>(),
          parkId = document["parkId"] as String)
    } else {
      Log.e("FirestoreError", "Error converting document to event: Document does not exist.")
      null
    }
  }
}
