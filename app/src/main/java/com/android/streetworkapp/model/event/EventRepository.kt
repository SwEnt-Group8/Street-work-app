package com.android.streetworkapp.model.event

/** A repository interface for event data. */
interface EventRepository {

  fun getNewEid(): String

  fun getEvents(onSuccess: (List<Event>) -> Unit, onFailure: (Exception) -> Unit)

  suspend fun addEvent(event: Event)
}
