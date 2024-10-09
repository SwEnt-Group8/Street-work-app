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
 */
data class Event(
    val eid: String,
    val title: String,
    val description: String,
    val participants: Int,
    val maxParticipants: Int,
    val date: Timestamp,
    val owner: String,
)

/**
 * Represents a list of events.
 *
 * @param events The list of events.
 */
data class EventList(val events: List<Event>)
