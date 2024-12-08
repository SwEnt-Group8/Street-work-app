package com.android.streetworkapp.model.park

import com.android.streetworkapp.model.parklocation.ParkLocation

/** A repository interface for park data. */
interface ParkRepository {

  fun getNewPid(): String

  suspend fun getParkByPid(pid: String): Park?

  suspend fun getParkByLocationId(locationId: String): Park?

  suspend fun createPark(park: Park)

  suspend fun getOrCreateParkByLocation(location: ParkLocation): Park?

  fun updateName(pid: String, name: String)

  suspend fun updateImageReference(pid: String, imageReference: String)

  suspend fun deleteRating(pid: String, rating: Int)

  suspend fun updateCapacity(pid: String, capacity: Int)

  suspend fun incrementOccupancy(pid: String)

  suspend fun decrementOccupancy(pid: String)

  suspend fun addEventToPark(pid: String, eid: String)

  suspend fun deleteEventFromPark(pid: String, eid: String)

  suspend fun addRating(pid: String, uid: String, rating: Float)

  suspend fun addImagesCollection(pid: String, collectionId: String)

  suspend fun deleteParkByPid(pid: String)
}
