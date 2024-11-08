package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation

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
 * @param votersUIDs The list of UIDs of users who have voted.
 */
data class Park(
    val pid: String,
    var name: String,
    var location: ParkLocation,
    var imageReference: String,
    var rating: Float = 0f,
    var nbrRating: Int = 0,
    var capacity: Int,
    var occupancy: Int,
    var events: List<String>,
    var votersUIDs: List<String> = emptyList()
)
