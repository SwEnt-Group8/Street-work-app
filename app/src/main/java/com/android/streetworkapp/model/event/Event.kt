package com.android.streetworkapp.model.event

import com.google.firebase.Timestamp

/**
 * Represents an event.
 *
 * @param eid The event ID.
 * @param title The event title.
 * @param description The event description.
 * @param participants The number of participants.
 * @param maxParticipants The maximum number of participants.
 * @param date The event date.
 * @param owner The event owner.
 * @param listParticipants The list of id of users.
 * @param parkId The id of the park for this event.
 */
data class Event(
    val eid: String,
    var title: String,
    var description: String,
    var participants: Int,
    var maxParticipants: Int,
    var date: Timestamp,
    var owner: String,
    var listParticipants: List<String> = emptyList(),
    var parkId: String = "Unknown Park",
    var status: EventStatus = EventStatus.CREATED
)

/**
 * Represents a list of events.
 *
 * @param events The list of events.
 */
data class EventList(val events: List<Event>)

/** Constant values for events. */
object EventConstants {
  const val MAX_NUMBER_PARTICIPANTS = 10
  const val MIN_NUMBER_PARTICIPANTS = 2
}

enum class EventStatus {
  CREATED,
  STARTED,
  ENDED
}
