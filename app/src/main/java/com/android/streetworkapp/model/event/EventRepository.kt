package com.android.streetworkapp.model.event

import com.android.streetworkapp.model.park.Park

/** A repository interface for event data. */
interface EventRepository {

  fun getNewEid(): String

  suspend fun getEventByEid(eid: String): Event?

  suspend fun getEvents(
      park: Park,
      onSuccess: (List<Event>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  suspend fun addParticipantToEvent(eid: String, uid: String)

  suspend fun removeParticipantFromEvent(eid: String, uid: String)

  suspend fun removeParticipantFromAllEvents(uid: String)

  suspend fun addEvent(event: Event)
}
