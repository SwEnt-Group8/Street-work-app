package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation

/**
 * Create a new default park given the park ID and location
 *
 * @param pid The park ID.
 * @param parkLocation The park location.
 */
fun createDefaultPark(pid: String, parkLocation: ParkLocation): Park {
  return Park(
      pid = pid,
      name = "Default Park ${parkLocation.id}",
      location = parkLocation,
      imageReference = "",
      rating = 1f,
      nbrRating = 0,
      capacity = 1,
      occupancy = 0,
      events = emptyList())
}
