package com.android.streetworkapp.model.event

import android.util.Log
import com.android.streetworkapp.model.park.Park
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** A repository interface using Firestore for park data. */
class EventRepositoryFirestore(private val db: FirebaseFirestore) : EventRepository {

  companion object {
    private const val COLLECTION_PATH = "events"
    private const val FIELD_PARTICIPANTS = "participants"
    private const val FIELD_LIST_PARTICIPANTS = "listParticipants"
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

  /**
   * Delete an event from the database.
   *
   * @param event The event to delete.
   */
  override suspend fun deleteEvent(event: Event) {
    require(event.eid.isNotEmpty())
    try {
      db.collection(COLLECTION_PATH).document(event.eid).delete().await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error deleting event: ${e.message}")
    }
  }

  /**
   * Add a participant to an event.
   *
   * @param eid The event ID.
   * @param uid The user ID.
   */
  override suspend fun addParticipantToEvent(eid: String, uid: String) {
    require(eid.isNotEmpty())
    require(uid.isNotEmpty())
    try {
      val event = getEventByEid(eid)
      if (event != null) {
        db.collection(COLLECTION_PATH)
            .document(eid)
            .update(
                FIELD_PARTICIPANTS,
                FieldValue.increment(1),
                FIELD_LIST_PARTICIPANTS,
                FieldValue.arrayUnion(uid))
            .await()
      } else {
        Log.e("FirestoreError", "Error adding participant to event: Event does not exist.")
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error adding participant to event: ${e.message}")
    }
  }

  /**
   * Remove a participant from an event.
   *
   * @param eid The event ID.
   * @param uid The user ID.
   */
  override suspend fun removeParticipantFromEvent(eid: String, uid: String) {
    require(eid.isNotEmpty())
    require(uid.isNotEmpty())
    try {
      val event = getEventByEid(eid)
      if (event != null) {
        db.collection(COLLECTION_PATH)
            .document(eid)
            .update(
                FIELD_PARTICIPANTS,
                FieldValue.increment(-1),
                FIELD_LIST_PARTICIPANTS,
                FieldValue.arrayRemove(uid))
            .await()
      } else {
        Log.e("FirestoreError", "Error removing participant from event: Event does not exist.")
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error removing participant from event: ${e.message}")
    }
  }

  /**
   * Update the status of an event.
   *
   * @param eid The event ID.
   * @param status The new status.
   */
  override suspend fun updateStatus(eid: String, status: EventStatus) {
    require(eid.isNotEmpty())
    try {
      db.collection(COLLECTION_PATH).document(eid).update("status", status).await()
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error updating event status: ${e.message}")
    }
  }

  /**
   * Remove a participant from all events and delete events where the user is the owner.
   *
   * @param uid The user ID.
   */
  override suspend fun removeParticipantFromAllEvents(uid: String) {
    require(uid.isNotEmpty())
    try {
      val events =
          db.collection(COLLECTION_PATH)
              .whereArrayContains(FIELD_LIST_PARTICIPANTS, uid)
              .get()
              .await()

      for (document in events.documents) {
        val event = documentToEvent(document)
        if (event != null) {
          if (event.owner == uid) {
            db.collection(COLLECTION_PATH).document(event.eid).delete().await()
          } else {
            db.collection(COLLECTION_PATH)
                .document(event.eid)
                .update(
                    FIELD_PARTICIPANTS,
                    FieldValue.increment(-1),
                    FIELD_LIST_PARTICIPANTS,
                    FieldValue.arrayRemove(uid))
                .await()
          }
        }
      }
    } catch (e: Exception) {
      Log.e("FirestoreError", "Error removing participant from all events: ${e.message}")
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
          parkId = document["parkId"] as String,
          status = EventStatus.valueOf(document["status"] as String))
    } else {
      Log.e("FirestoreError", "Error converting document to event: Document does not exist.")
      null
    }
  }
}
