package com.android.streetworkapp.model.event

/** A repository interface for event data. */
interface EventRepository {

  fun getNewEid(): String

  suspend fun addEvent(event: Event)
}
