package com.android.streetworkapp.model.event

import com.google.firebase.Timestamp

data class Event(
    val eid: String,
    val title: String,
    val description: String,
    val participants: Int,
    val maxParticipants: Int,
    val date: Timestamp,
    val owner: String,
)

data class EventList(val events: List<Event>)
