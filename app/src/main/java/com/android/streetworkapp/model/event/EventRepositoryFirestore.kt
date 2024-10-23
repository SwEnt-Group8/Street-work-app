package com.android.streetworkapp.model.event

import android.util.Log
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
