package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.event.EventList

data class Park(
    val pid: String,
    val name: String,
    val location: String, // TODO: Change to ParkLocation
    val image: String,
    val rating: Float,
    val events: EventList
)
