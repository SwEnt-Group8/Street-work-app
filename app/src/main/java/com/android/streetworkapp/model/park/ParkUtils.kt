package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation

/**
 * Create a new default park given the park ID and location
 *
 * @param pid The park ID.
 * @param lon The park longitude.
 * @param lat The park latitude.
 * @param locationId The park location ID.
 */
fun createDefaultPark(pid: String, lon: Double, lat: Double, locationId: String): Park {
  return Park(
      pid = pid,
      name = "Default Park $locationId",
      location = ParkLocation(lon, lat, locationId),
      imageReference = "",
      rating = 1f,
      nbrRating = 0,
      capacity = 1,
      occupancy = 0,
      events = emptyList())
}
