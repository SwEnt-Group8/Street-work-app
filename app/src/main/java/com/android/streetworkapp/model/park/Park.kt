package com.android.streetworkapp.model.park

import android.media.Image
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
 */
data class Park(
    val pid: String,
    var name: String,
    var location: ParkLocation,
    var image: Image?,
    var rating: Float,
    var nbrRating: Int,
    var occupancy: Float,
    var events: List<String>
)
