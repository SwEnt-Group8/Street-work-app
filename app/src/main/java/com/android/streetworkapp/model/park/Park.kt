package com.android.streetworkapp.model.park

import androidx.compose.ui.graphics.painter.Painter
import com.android.streetworkapp.model.event.EventList

/**
 * Represents a park.
 *
 * @param pid The park ID.
 * @param name The park name.
 * @param location The park location.
 * @param image The park image.
 * @param rating The park rating.
 * @param nbrRating The number of ratings.
 * @param occupancy The park occupancy.
 * @param events The list of events in the park.
 */
data class Park(
    val pid: String,
    val name: String,
    val location: String, // TODO: Change to ParkLocation
    val image: Painter,
    val rating: Float,
    val nbrRating: Int,
    val occupancy: Float,
    val events: EventList
)
